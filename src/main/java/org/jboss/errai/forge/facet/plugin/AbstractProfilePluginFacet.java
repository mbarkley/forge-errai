package org.jboss.errai.forge.facet.plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;

abstract class AbstractProfilePluginFacet extends AbstractBaseFacet {

  protected DependencyArtifact pluginArtifact;
  protected Collection<DependencyBuilder> dependencies;
  protected Collection<ConfigurationElement> configurations;
  protected Collection<PluginExecution> executions;
  protected boolean extensions = true;
  
  @Override
  public boolean install() {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    Model pom = coreFacet.getPOM();
    Profile profile = getProfile(MAIN_PROFILE, pom.getProfiles());
    final VersionOracle oracle = new VersionOracle(getProject().getFacet(DependencyFacet.class));
    
    if (profile == null) {
      makeProfile(MAIN_PROFILE, Collections.<DependencyBuilder> emptyList(), oracle);
      pom = coreFacet.getPOM();
      profile = getProfile(MAIN_PROFILE, pom.getProfiles());
    }
    
    if (profile.getBuild() == null) {
      profile.setBuild(new BuildBase());
    }

    Plugin plugin = getPlugin(pluginArtifact, profile.getBuild().getPlugins());
 
    if (plugin == null) {
      plugin = new Plugin();
      plugin.setArtifactId(pluginArtifact.getArtifactId());
      plugin.setGroupId(pluginArtifact.getGroupId());
      plugin.setVersion(oracle.resolveVersion(plugin.getGroupId(), plugin.getArtifactId()));
      profile.getBuild().addPlugin(plugin);
    }
    
    final MavenPluginAdapter adapter = new MavenPluginAdapter(plugin);
    final Configuration config = adapter.getConfig();
    for (final ConfigurationElement elem : configurations) {
      config.addConfigurationElement(elem);
    }
    adapter.setConfig(config);
    
    for (final PluginExecution exec : executions) {
      adapter.addExecution(exec);
    }
    adapter.setExtensions(extensions);
    
    // Changes are not committed from adater to original plugin
    plugin.setConfiguration(adapter.getConfiguration());
    plugin.setExecutions(adapter.getExecutions());
    
    coreFacet.setPOM(pom);

    return true;
  }

  private Plugin getPlugin(DependencyArtifact pluginArtifact, List<Plugin> plugins) {
    for (final Plugin plugin : plugins) {
      if (pluginArtifact.getGroupId().equals(plugin.getGroupId())
              && pluginArtifact.getArtifactId().equals(plugin.getArtifactId())) {
        return plugin;
      }
    }

    return null;
  }

}
