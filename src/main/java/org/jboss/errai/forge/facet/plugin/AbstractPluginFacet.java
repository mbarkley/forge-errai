package org.jboss.errai.forge.facet.plugin;

import java.util.Collection;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.jboss.errai.forge.constant.ArtifactVault;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.MavenPlugin;
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
      // So that it is not duplicated when added later on
      pluginFacet.removePlugin(pluginDep);
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
      if (dep.getVersion() == null || dep.getVersion().equals("")) {
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
  public boolean isInstalled() {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();
    if (pom.getBuild() == null)
      return false;

    final Plugin plugin = pom.getBuild().getPluginsAsMap().get(pluginArtifact.toString());

    if (plugin == null)
      return false;

    outer: for (final DependencyBuilder dep : dependencies) {
      for (final org.apache.maven.model.Dependency pluginDep : plugin.getDependencies()) {
        if (dep.getArtifactId().equals(pluginDep.getArtifactId()) && dep.getGroupId().equals(pluginDep.getGroupId()))
          continue outer;
      }
      return false;
    }

    final MavenPluginFacet pluginFacet = getProject().getFacet(MavenPluginFacet.class);
    final MavenPlugin mPlugin = pluginFacet.getPlugin(DependencyBuilder.create(pluginArtifact.toString()));

    outer: for (final Execution exec : executions) {
      for (final Execution pluginExec : mPlugin.listExecutions()) {
        // TODO check more than just id
        if (exec.getId().equals(pluginExec.getId()))
          continue outer;
      }
      return false;
    }

    if (!isMatchingConfiguration(mPlugin.getConfig(), configurations))
      return false;

    return true;
  }

  protected boolean isMatchingConfiguration(final Configuration config, final Collection<ConfigurationElement> elements) {
    for (final ConfigurationElement elem : elements) {
      if (!isMatchingElement(config.getConfigurationElement(elem.getName()), elem))
        return false;
    }

    return true;
  }

  /**
   * Checks that the given {@link ConfigurationElement} is consistent with the
   * expected one. This means that the expected configuration tree is a subtree
   * of the given.
   * 
   * @param given
   *          The given configuration element.
   * @param expected
   *          The expected configuration element.
   * @return True if expected is a subtree of given.
   */
  private boolean isMatchingElement(final ConfigurationElement given, final ConfigurationElement expected) {
    if (given == null)
      return false;

    if (expected.hasChilderen()) {
      for (final PluginElement pluginElem : expected.getChildren()) {
        if (pluginElem instanceof ConfigurationElement) {
          final ConfigurationElement elem = ConfigurationElement.class.cast(pluginElem);
          ConfigurationElement child;
          int i;
          for (i = 0; i < given.getChildren().size(); i++) {
            child = ConfigurationElement.class.cast(given.getChildren().get(i));
            if (child.getName().equals(elem.getName()) && isMatchingElement(child, elem)) {
              return true;
            }
          }
          if (i == given.getChildren().size())
            return false;
        }
      }

      return true;
    }
    else {
      return expected.getText().equals(given.getText());
    }
  }

  @Override
  public boolean uninstall() {
    final MavenPluginFacet pluginFacet = getProject().getFacet(MavenPluginFacet.class);
    pluginFacet.removePlugin(DependencyBuilder.create(pluginArtifact.toString()));

    return true;
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
