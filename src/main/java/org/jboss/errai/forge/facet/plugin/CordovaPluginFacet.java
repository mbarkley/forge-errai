package org.jboss.errai.forge.facet.plugin;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.maven.model.PluginExecution;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;

public class CordovaPluginFacet extends AbstractProfilePluginFacet {

  public CordovaPluginFacet() {
    profileId = "mobile";
    pluginArtifact = DependencyArtifact.CordovaPlugin;
    dependencies = new ArrayList<DependencyBuilder>(0);

    final PluginExecution execution = new PluginExecution();
    execution.setId("build");
    execution.setPhase("package");
    execution.addGoal("build-project");

    executions = Arrays.asList(new PluginExecution[] { execution });
    configurations = Arrays.asList(new ConfigurationElement[] {
        ConfigurationElementBuilder.create().setName("source").setText("1.6"),
        ConfigurationElementBuilder.create().setName("target").setText("1.6")
    });
  }

}
