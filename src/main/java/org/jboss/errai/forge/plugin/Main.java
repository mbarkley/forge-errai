package org.jboss.errai.forge.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.maven.model.Build;
import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.ProjectConfigFactory;
import org.jboss.errai.forge.config.SerializableSet;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections;
import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections.Feature;
import org.jboss.errai.forge.facet.aggregate.BaseAggregatorFacet.UninstallationExecption;
import org.jboss.errai.forge.facet.aggregate.CoreFacet;
import org.jboss.errai.forge.facet.base.CoreBuildFacet;
import org.jboss.errai.forge.facet.module.ModuleCoreFacet;
import org.jboss.errai.forge.util.FeatureCompleter;
import org.jboss.errai.forge.util.ShellPrintFormatter;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.xml.sax.SAXException;

/**
 * The Errai Forge Plugin implementation. Configures the Maven pom file, GWT
 * Module, and other configuration files for adding various Errai features to a
 * maven project.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Alias("errai-setup")
@RequiresFacet({ CoreFacet.class })
@Help(value = "Add dependencies and other configurations for Errai to a Maven project.")
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

  @SetupCommand(help = "Install the core components used by all Errai features.")
  public void setup(PipeOut out) throws ParserConfigurationException, SAXException, IOException, TransformerException {
    // This facet is prerequisite for installing other required facets (used in
    // getExistingModules)
    if (!project.hasFacet(JavaSourceFacet.class)) {
      installEvent.fire(new InstallFacets(JavaSourceFacet.class));
    }

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
    if (config.getProjectProperty(ProjectProperty.MODULE_NAME, String.class) == null) {
      config.setProjectProperty(ProjectProperty.MODULE_NAME, promptForModuleName());
    }

    // Configure errai version
    if (config.getProjectProperty(ProjectProperty.ERRAI_VERSION, String.class) == null) {
      final String version = promptForErraiVersion();
      config.setProjectProperty(ProjectProperty.ERRAI_VERSION, version);
    }

    addFacet(out, CoreFacet.class);
  }

  /**
   * Prompt the user for a module name, set the "rename-to" attribute in the
   * file, and return the new name.
   */
  private String promptForModuleName() throws ParserConfigurationException, SAXException, IOException,
          TransformerException {
    if (!project.hasFacet(ModuleCoreFacet.class))
      installEvent.fire(new InstallFacets(ModuleCoreFacet.class));

    final ModuleCoreFacet moduleFacet = project.getFacet(ModuleCoreFacet.class);
    String moduleName = moduleFacet.getModuleName();
    final boolean response = shell.promptBoolean(
            String.format("Would you like to rename the module? (Current name: %s)", moduleName),
            false);

    if (response) {
      moduleName = shell.prompt("Enter the new module name.");
      moduleFacet.setModuleName(moduleName);
    }

    return moduleName;
  }

  private List<String> getExistingModules() {
    final JavaSourceFacet sourceFacet = project.getFacet(JavaSourceFacet.class);
    // XXX could a gwt module also be in a resource folder?
    final List<DirectoryResource> sourceFolder = sourceFacet.getSourceFolders();

    final List<String> retVal = new ArrayList<String>();

    for (final DirectoryResource dir : sourceFolder) {
      if (dir.exists()) {
        final File underlyingDir = dir.getUnderlyingResourceObject();
        final Collection<File> found = findGwtModuleFiles(underlyingDir);
        for (File file : found) {
          String relPath = file.getAbsolutePath().replace(underlyingDir.getAbsolutePath(), "");
          if (relPath.charAt(0) == File.separatorChar)
            relPath = relPath.substring(1);

          retVal.add(relPath.replace(File.separatorChar, '.').replaceFirst("\\.gwt\\.xml$", ""));
        }
      }
    }

    return retVal;
  }

  private static Collection<File> findGwtModuleFiles(final File f) {
    if (f.exists()) {
      if (f.isDirectory()) {
        Collection<File> retVal = new LinkedList<File>();
        for (final File child : f.listFiles()) {
          final Collection<File> result = findGwtModuleFiles(child);
          if (result.size() > 0) {
            retVal.addAll(result);
          }
        }

        return retVal;
      }
      else if (f.isFile()) {
        if (f.getName().endsWith(".gwt.xml")) {
          final Collection<File> retVal = new LinkedList<File>();
          retVal.add(f);

          return retVal;
        }
      }
    }

    return new LinkedList<File>();
  }

  private String promptForModule() {
    final List<String> modules = getExistingModules();
    if (!modules.isEmpty()) {
      modules.add("Create new module");
      int choice = shell.promptChoice("Please select a GWT module in which to add Errai dependencies.", modules);

      if (choice < modules.size() - 1) {
        return modules.get(choice);
      }
    }

    return promptForNewModuleName();
  }

  private String promptForNewModuleName() {
    final String moduleName = shell.prompt("Please type the logical name for your new module.");
    // TODO check if name is valid and re-prompt
    return moduleName;
  }

  private String promptForErraiVersion() {
    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    final Dependency erraiDep = DependencyBuilder.create(DependencyArtifact.ErraiParent.toString());
    final List<String> versions = new ArrayList<String>();
    for (final Dependency dep : depFacet.resolveAvailableVersions(erraiDep)) {
      if (isValidVersion(dep.getVersion()))
        versions.add(dep.getVersion());
    }
    int choice = shell.promptChoice("Please select a version of Errai.", versions);

    return versions.get(choice);
  }

  private boolean isValidVersion(final String version) {
    final Integer majorVersion;

    try {
      majorVersion = Integer.valueOf(version.substring(0, 1));
    }
    catch (NumberFormatException e) {
      return false;
    }

    return (majorVersion > 3
    || (majorVersion == 3
            && (version.contains("SNAPSHOT")) || version.compareTo("3.0.0.20131205-M3") > 0));
  }

  private File moduleLogicalNameToFile(final String moduleName) {
    final String relModuleFile = moduleName.replace('.', File.separatorChar) + ".gwt.xml";
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    final Build build = coreFacet.getPOM().getBuild();

    final String relSrcRoot = (build == null || build.getSourceDirectory() == null) ?
            CoreBuildFacet.DEFAULT_SRC_DIRECTORY :
            build.getSourceDirectory();

    final File modulePath = new File(new File(project.getProjectRoot().getUnderlyingResourceObject(), relSrcRoot),
            relModuleFile);

    return modulePath;
  }

  private boolean addFacet(final PipeOut out, final Class<? extends Facet> facetType) {
    if (!project.hasFacet(facetType)) {
      installEvent.fire(new InstallFacets(facetType));

      return project.hasFacet(facetType);
    }
    else {
      ShellMessages.info(out, facetType.getSimpleName() + " has already been added to this project.");

      return false;
    }
  }

  @Command(value = "list-features", help = "Display a list of Errai features.")
  public void listFeatures(
          final PipeOut out,
          @Option(name = "verbose", shortName = "v", flagOnly = true, help = "Display a short description of each feature.") final Boolean verbose,
          @Option(name = "installed", shortName = "i", flagOnly = true, help = "Only show currently installed features.") final Boolean installed) {
    for (final Feature feature : aggregatorReflections.iterable()) {
      if (!installed || project.hasFacet(feature.getFeatureClass()))
        printFeatureInfo(out, feature, verbose);
    }
  }

  private void printFeatureInfo(final PipeOut out, final Feature feature, final Boolean verbose) {
    final int width = 15;

    ShellPrintFormatter.printTitle(out, "Feature:", width);
    out.println(feature.getName());

    ShellPrintFormatter.printTitle(out, "Short Name:", width);
    out.println(feature.getShortName());

    if (verbose) {
      ShellPrintFormatter.printTitle(out, "Description:", width);
      out.println(feature.getDescription());
    }

    out.println();
  }

  @Command(value = "add-feature", help = "Add an Errai feature to your project. See list-features for an inventory of features.")
  public void addFeature(
          final PipeOut out,
          @Option(required = true, completer = FeatureCompleter.class, help = "The short name of the feature to add.") final String featureName) {
    final Feature feature = aggregatorReflections.getFeature(featureName);
    if (feature != null) {
      final boolean result = addFacet(out, feature.getFeatureClass());

      if (result) {
        final ProjectConfig config = configFactory.getProjectConfig(project);
        SerializableSet set = config.getProjectProperty(ProjectProperty.INSTALLED_FEATURES, SerializableSet.class);

        if (set == null)
          set = new SerializableSet();

        set.add(feature.getShortName());
        config.setProjectProperty(ProjectProperty.INSTALLED_FEATURES, set);
      }
    }
    else {
      printUnregcoznizedFeatureError(out, featureName);
    }
  }

  @Command(value = "remove-feature", help = "Remove an Errai feature from this project.")
  public void removeFeature(
          final PipeOut out,
          @Option(required = true, completer = FeatureCompleter.class, help = "The short name of the feature to remove.") final String featureName) {
    final Feature feature = aggregatorReflections.getFeature(featureName);
    if (feature != null) {
      if (!project.hasFacet(feature.getFeatureClass())) {
        ShellPrintFormatter.printError(out, String.format("The feature %s is not installed.", feature.getShortName()));
        return;
      }

      boolean uninstallResult = false;

      try {
        uninstallResult = project.getFacet(feature.getFeatureClass()).uninstallRequirements();
      }
      catch (UninstallationExecption e) {
        ShellPrintFormatter.printError(out, e.getMessage());
      }

      if (!uninstallResult) {
        ShellPrintFormatter.printError(out,
                String.format("Could not remove some of the required projects for %s.", feature.getShortName()));
        return;
      }

      if (!project.hasFacet(feature.getFeatureClass())) {
        final ProjectConfig config = configFactory.getProjectConfig(project);
        final SerializableSet directlyInstalled = config.getProjectProperty(ProjectProperty.INSTALLED_FEATURES,
                SerializableSet.class);
        directlyInstalled.remove(featureName);

        config.setProjectProperty(ProjectProperty.INSTALLED_FEATURES, directlyInstalled);
      }
    }
    else {
      printUnregcoznizedFeatureError(out, featureName);
    }
  }

  private void printUnregcoznizedFeatureError(final PipeOut out, final String name) {
    ShellPrintFormatter.printError(out,
            String.format("Unrecognized feature name, %s. See %s for available features.", name,
                    "list-features"));
  }
}
