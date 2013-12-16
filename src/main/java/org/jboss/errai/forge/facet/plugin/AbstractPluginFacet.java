package org.jboss.errai.forge.facet.plugin;

import java.util.Collection;

import org.jboss.errai.forge.constant.ArtifactVault.PluginArtifact;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ MavenPluginFacet.class, DependencyFacet.class })
abstract class AbstractPluginFacet extends BaseFacet {

  protected PluginArtifact pluginArtifact;
  protected Collection<ConfigurationElement> configurations;
  protected Collection<DependencyBuilder> dependencies;
  protected Collection<Execution> executions;
  
  @Override
  public boolean install() {
    // TODO logging and error-handling
    
    final MavenPluginFacet pluginFacet = getProject().getFacet(MavenPluginFacet.class);
    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);
    final VersionOracle oracle = new VersionOracle(depFacet);
    final Dependency pluginDep = DependencyBuilder.create(pluginArtifact.toString()).setVersion(
            oracle.resolveVersion(pluginArtifact));
    final MavenPluginBuilder plugin;
    
    if (pluginFacet.hasPlugin(pluginDep)) {
      plugin = MavenPluginBuilder.create(pluginFacet.getPlugin(pluginDep));
    }
    else {
      plugin = MavenPluginBuilder.create();
      pluginFacet.addPlugin(plugin);
    }

    Configuration config = plugin.getConfig();
    for (final ConfigurationElement configElem : configurations) {
      config.addConfigurationElement(configElem);
    }
    
    for (final DependencyBuilder dep : dependencies) {
      if (dep.getVersion() == null || dep.getVersion().equals(""))
        dep.setVersion(oracle.resolveVersion(dep.getGroupId(), dep.getArtifactId()));
      plugin.addPluginDependency(dep);
    }
    
    for (final Execution exec : executions) {
      plugin.addExecution(exec);
    }
    
    return true;
  }

  @Override
  public boolean isInstalled() {
    return getProject().hasFacet(getClass());
  }

  @Override
  public boolean uninstall() {
    // TODO implement
    return false;
  }

}
