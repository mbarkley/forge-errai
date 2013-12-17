package org.jboss.errai.forge.plugin;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.forge.facet.dependency.ErraiBuildDependencyFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;

@Alias("errai-setup")
@RequiresFacet({ ErraiBuildDependencyFacet.class })
public class Main implements Plugin {
  
  @Inject
  private Event<InstallFacets> installEvent;
  
  @Inject
  private Project project;
  
  @SetupCommand
  public void setup(PipeOut out) {
    if (!project.hasFacet(ErraiBuildDependencyFacet.class)) {
      installEvent.fire(new InstallFacets(ErraiBuildDependencyFacet.class));
    }
    else {
      ShellMessages.info(out, "The plugin is already setup.");
    }
  }
  
  @Command("version")
  public void pipeVersion(PipeOut out) {
    ShellMessages.info(out, "1.0.0-SNAPSHOT");
  }

}