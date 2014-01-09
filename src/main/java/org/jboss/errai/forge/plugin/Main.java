package org.jboss.errai.forge.plugin;

import java.io.File;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfigFactory;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.facet.aggregate.CoreFacet;
import org.jboss.errai.forge.facet.aggregate.ErraiMessagingFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
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

    if (!project.hasFacet(CoreFacet.class)) {
      installEvent.fire(new InstallFacets(CoreFacet.class));
    }
  }

  private String promptForModule() {
    final String moduleName = shell.prompt("Please type the logical name for your module.");
    // TODO check if name is valid and re-prompt
    return moduleName;
  }

  private File moduleLogicalNameToFile(final String moduleName) {
    final String relModuleFile = moduleName.replace('.', File.separatorChar) + ".gwt.xml";

    // TODO make this dynamic to project settings
    final String relSrcRoot = "src/main/java";

    final File modulePath = new File(new File(project.getProjectRoot().getUnderlyingResourceObject(), relSrcRoot),
            relModuleFile);

    return modulePath;
  }

  @Command("add-core")
  public void addCore(PipeOut out) {
    if (!project.hasFacet(CoreFacet.class)) {
      installEvent.fire(new InstallFacets(CoreFacet.class));
    }
    else {
      ShellMessages.info(out, "The plugin is already setup.");
    }
  }

  @Command("version")
  public void pipeVersion(PipeOut out) {
    ShellMessages.info(out, "1.0.0-SNAPSHOT");
  }

  @Command("add-messaging")
  public void addMessaging(PipeOut out) {
    if (!project.hasFacet(ErraiMessagingFacet.class)) {
      installEvent.fire(new InstallFacets(ErraiMessagingFacet.class));
    }
    else {
      ShellMessages.info(out, "Errai Messaging has already been added to this project.");
    }
  }

}
