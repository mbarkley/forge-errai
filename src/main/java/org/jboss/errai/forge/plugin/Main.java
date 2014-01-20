package org.jboss.errai.forge.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.ProjectConfigFactory;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections;
import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections.Feature;
import org.jboss.errai.forge.facet.aggregate.CoreFacet;
import org.jboss.errai.forge.util.FeatureCompleter;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;

/**
 * The Errai Forge Plugin implementation. Configures the Maven pom file, GWT
 * Module, and other configuration files for adding various Errai features to a
 * maven project.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Alias("errai-setup")
@RequiresFacet({ CoreFacet.class })
public class Main implements Plugin {

  @Inject
  private Event<InstallFacets> installEvent;

  @Inject
  private Project project;

  @Inject
  private Shell shell;

  @Inject
  private ProjectConfigFactory configFactory;

  @Inject
  private AggregatorFacetReflections aggregatorReflections;

  @SetupCommand
  public void setup(PipeOut out) {
    final ProjectConfig config = configFactory.getProjectConfig(project);

    // Configure gwt module
    if (config.getProjectProperty(ProjectProperty.MODULE_LOGICAL, String.class) == null) {
      final String module = promptForModule();
      config.setProjectProperty(ProjectProperty.MODULE_LOGICAL, module);
    }
    if (config.getProjectProperty(ProjectProperty.MODULE_FILE, File.class) == null) {
      final File modulePath = moduleLogicalNameToFile(config.getProjectProperty(ProjectProperty.MODULE_LOGICAL,
              String.class));
      config.setProjectProperty(ProjectProperty.MODULE_FILE, modulePath);
    }

    // Configure errai version
    if (config.getProjectProperty(ProjectProperty.ERRAI_VERSION, String.class) == null) {
      final String version = promptForErraiVersion();
      config.setProjectProperty(ProjectProperty.ERRAI_VERSION, version);
    }

    addFacet(out, CoreFacet.class);
  }

  private String promptForModule() {
    final String moduleName = shell.prompt("Please type the logical name for your module.");
    // TODO check if name is valid and re-prompt
    return moduleName;
  }

  private String promptForErraiVersion() {
    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    final Dependency erraiDep = DependencyBuilder.create(DependencyArtifact.ErraiParent.toString());
    final List<String> versions = new ArrayList<String>();
    for (final Dependency dep : depFacet.resolveAvailableVersions(erraiDep)) {
      // TODO refactor minimum supported version into separate method and
      // improve logic
      final Integer majorVersion = Integer.valueOf(dep.getVersion().substring(0, 1));
      if (majorVersion >= 3)
        versions.add(dep.getVersion());
    }
    int choice = shell.promptChoice("Please select a version of Errai.", versions);

    return versions.get(choice);
  }

  private File moduleLogicalNameToFile(final String moduleName) {
    final String relModuleFile = moduleName.replace('.', File.separatorChar) + ".gwt.xml";

    // TODO make this dynamic to project settings
    final String relSrcRoot = "src/main/java";

    final File modulePath = new File(new File(project.getProjectRoot().getUnderlyingResourceObject(), relSrcRoot),
            relModuleFile);

    return modulePath;
  }

  private void addFacet(final PipeOut out, final Class<? extends Facet> facetType) {
    if (!project.hasFacet(facetType)) {
      installEvent.fire(new InstallFacets(facetType));
    }
    else {
      ShellMessages.info(out, facetType.getSimpleName() + " has already been added to this project.");
    }
  }

  @Command("version")
  public void pipeVersion(PipeOut out) {
    ShellMessages.info(out, "1.0.0-SNAPSHOT");
  }

  @Command("list-features")
  public void listFeatures(final PipeOut out,
          @Option(name = "verbose", shortName = "v", flagOnly = true) final Boolean verbose) {
    for (final Feature feature : aggregatorReflections.iterable()) {
      printFeatureInfo(out, feature, verbose);
    }
  }

  public void printFeatureInfo(final PipeOut out, final Feature feature, final Boolean verbose) {
    out.println(String.format("%s : %s", feature.getShortName(), feature.getName()));
    if (verbose) {
      out.println(String.format("\t%s", feature.getDescription()));
    }
  }

  @Command("add-feature")
  public void addFeature(final PipeOut out,
          @Option(required = true, completer = FeatureCompleter.class) final String featureName) {
    final Feature feature = aggregatorReflections.getFeature(featureName);
    if (feature != null) {
      addFacet(out, feature.getFeatureClass());
    }
    else {
      out.print(ShellColor.RED, "Error: ");
      out.println(String.format("Unrecognized feature name, %s. See %s for available features.", featureName,
              "list-features"));
    }
  }
}
