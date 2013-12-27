package org.jboss.errai.forge.facet.plugin;

import java.util.Collection;

import org.jboss.errai.forge.constant.ArtifactVault;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.maven.plugins.PluginElement;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;

abstract class AbstractPluginFacet extends AbstractBaseFacet {

  protected DependencyArtifact pluginArtifact;
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
      plugin.setDependency(pluginDep);
    }

    Configuration config = plugin.getConfig();
    for (final ConfigurationElement configElem : configurations) {
      mergeConfigurationElement(config, configElem);
    }

    for (final DependencyBuilder dep : dependencies) {
      if (!ArtifactVault.isManaged(dep.getGroupId(), dep.getArtifactId())
              && (dep.getVersion() == null || dep.getVersion().equals(""))) {
        if (dep.getGroupId().equals(ArtifactVault.ERRAI_GROUP_ID))
          dep.setVersion(Property.ErraiVersion.invoke());
        else
          dep.setVersion(oracle.resolveVersion(dep.getGroupId(), dep.getArtifactId()));
      }
      plugin.addPluginDependency(dep);
    }

    for (final Execution exec : executions) {
      plugin.addExecution(exec);
    }
    pluginFacet.addPlugin(plugin);

    return true;
  }

  @Override
  public boolean uninstall() {
    // TODO implement
    return false;
  }

  protected void mergeConfigurationElement(final Configuration config, final ConfigurationElement configElem) {
    if (!config.hasConfigurationElement(configElem.getName())) {
      config.addConfigurationElement(configElem);
    }
    else {
      final ConfigurationElement prev = config.getConfigurationElement(configElem.getName());
      config.removeConfigurationElement(configElem.getName());
      config.addConfigurationElement(merge(prev, configElem));
    }
  }

  protected ConfigurationElement merge(ConfigurationElement prev, ConfigurationElement configElem) {
    // Replace text-only elements
    if (!prev.hasChilderen()) {
      return configElem;
    }
    else {
      final ConfigurationElementBuilder retVal = ConfigurationElementBuilder.create();
      // Copy non-conflicting elements from old config element
      for (final PluginElement child : prev.getChildren()) {
        if (!(child instanceof ConfigurationElement)) {
          // configElem should only contain other ConfigurationElemnents, so
          // this case is non-conflicting
          retVal.addChild(child);
        }
        else {
          final ConfigurationElement oldChild = ConfigurationElement.class.cast(child);
          if (!configElem.hasChildByName(oldChild.getName(), true))
            retVal.addChild(oldChild);
        }
      }
      // Add or merge from new config element
      for (final PluginElement child : configElem.getChildren()) {
        if (!(child instanceof ConfigurationElement)) {
          throw new IllegalArgumentException("Cannot merge PluginElement of type " + child.getClass().getName());
        }
        else {
          final ConfigurationElement newChild = ConfigurationElement.class.cast(child);
          if (prev.hasChildByName(newChild.getName(), true)) {
            retVal.addChild(merge(prev.getChildByName(newChild.getName(), true), newChild));
          }
          else {
            retVal.addChild(newChild);
          }
        }
      }

      return retVal;
    }
  }

}
