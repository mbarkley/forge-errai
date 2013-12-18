package org.jboss.errai.forge.facet.plugin;

import java.util.ArrayList;

import org.apache.maven.model.PluginExecution;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.facet.base.RequiresCore;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.project.dependencies.DependencyBuilder;

@RequiresCore
public class JbossPluginFacet extends AbstractProfilePluginFacet {

  public JbossPluginFacet() {
    pluginArtifact = DependencyArtifact.JbossPlugin;
    dependencies = new ArrayList<DependencyBuilder>(0);
    executions = new ArrayList<PluginExecution>(0);
    configurations = new ArrayList<ConfigurationElement>(0);
    extensions = false;
  }
}
