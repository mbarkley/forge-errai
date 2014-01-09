package org.jboss.errai.forge.facet.plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.constant.ArtifactVault;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.errai.forge.util.MavenConverter;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;

/**
 * A base class for facets that install Maven plugins within the
 * {@link AbstractBaseFacet#MAIN_PROFILE main profile} of the pom file. Concrete
 * subclasses must assign values to the fields
 * {@link AbstractPluginFacet#pluginArtifact pluginArtifact},
 * {@link AbstractPluginFacet#configurations configurations},
 * {@link AbstractPluginFacet#dependencies dependencies}, and
 * {@link AbstractProfilePluginFacet#executions executions}.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
abstract class AbstractProfilePluginFacet extends AbstractPluginFacet {

  /**
   * Executions for the installed plugin.
   */
  protected Collection<PluginExecution> executions;
  protected boolean extensions = true;

  @Override
  public boolean install() {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    Model pom = coreFacet.getPOM();
    Profile profile = getProfile(MAIN_PROFILE, pom.getProfiles());
    final VersionOracle oracle = new VersionOracle(getProject().getFacet(DependencyFacet.class));

    if (profile == null) {
      addDependenciesToProfile(MAIN_PROFILE, Collections.<DependencyBuilder> emptyList(), oracle);
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
      mergeConfigurationElement(config, elem);
    }
    adapter.setConfig(config);

    for (final DependencyBuilder depBuilder : dependencies) {
      if (depBuilder.getVersion() == null || depBuilder.getVersion().equals("")) {
        if (ArtifactVault.ERRAI_GROUP_ID.equals(depBuilder.getGroupId())) {
          depBuilder.setVersion(Property.ErraiVersion.invoke());
        }
        else {
          depBuilder.setVersion(new VersionOracle(getProject().getFacet(DependencyFacet.class)).resolveVersion(
                  depBuilder.getGroupId(), depBuilder.getArtifactId()));
        }
      }
      adapter.addDependency(MavenConverter.convert(depBuilder));
    }

    for (final PluginExecution exec : executions) {
      adapter.addExecution(exec);
    }
    adapter.setExtensions(extensions);

    // Changes are not committed from adapter to original plugin
    plugin.setConfiguration(adapter.getConfiguration());
    plugin.setExecutions(adapter.getExecutions());
    plugin.setDependencies(adapter.getDependencies());

    coreFacet.setPOM(pom);

    return true;
  }

  @Override
  public boolean isInstalled() {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();

    final Profile profile = getProfile(MAIN_PROFILE, pom.getProfiles());
    if (profile == null || profile.getBuild() == null)
      return false;

    final Plugin plugin = profile.getBuild().getPluginsAsMap().get(pluginArtifact.toString());

    outer: for (final DependencyBuilder dep : dependencies) {
      for (final Dependency pluginDep : plugin.getDependencies()) {
        if (pluginDep.getArtifactId().equals(dep.getArtifactId()) && pluginDep.getGroupId().equals(dep.getGroupId()))
          continue outer;
      }
      return false;
    }

    outer: for (final PluginExecution exec : executions) {
      for (final PluginExecution pluginExec : plugin.getExecutions()) {
        if (pluginExec.getId().equals(exec.getId()))
          continue outer;
      }
      return false;
    }

    final MavenPluginAdapter adapter = new MavenPluginAdapter(plugin);

    if (!isMatchingConfiguration(adapter.getConfig(), configurations))
      return false;

    return true;
  }

  @Override
  public boolean uninstall() {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();

    final Profile profile = getProfile(MAIN_PROFILE, pom.getProfiles());
    if (profile == null)
      return false;

    final BuildBase build = profile.getBuild();
    if (build == null)
      return false;

    final Plugin plugin = build.getPluginsAsMap().get(pluginArtifact.toString());
    if (plugin == null)
      return false;

    build.removePlugin(plugin);
    profile.setBuild(build);
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
