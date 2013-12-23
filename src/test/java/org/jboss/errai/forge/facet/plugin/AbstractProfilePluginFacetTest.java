package org.jboss.errai.forge.facet.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.parser.java.util.Assert;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.junit.Test;

public class AbstractProfilePluginFacetTest extends BasePluginFacetTest {

  public static class DefinitionOnly extends AbstractProfilePluginFacet {
    public DefinitionOnly() {
      pluginArtifact = DependencyArtifact.Clean;
      configurations = Collections.emptyList();
      dependencies = Collections.emptyList();
      executions = Collections.emptyList();
    }
  }

  public static class DependencyHavingPlugin extends AbstractProfilePluginFacet {
    public DependencyHavingPlugin() {
      pluginArtifact = DependencyArtifact.Clean;
      configurations = Collections.emptyList();
      executions = Collections.emptyList();
      // Do not put errai dependencies here. They will fail because the
      // errai.version propery is not set.
      dependencies = Arrays.asList(new DependencyBuilder[] { DependencyBuilder.create(DependencyArtifact.Guava
              .toString()) });
    }
  }

  public static class ConfigHavingPlugin extends AbstractProfilePluginFacet {
    public ConfigHavingPlugin() {
      pluginArtifact = DependencyArtifact.Clean;
      configurations = Arrays.asList(new ConfigurationElement[] {
          ConfigurationElementBuilder.create().setName("configName").setText("configText"),
          ConfigurationElementBuilder.create().setName("parent")
                  .addChild(ConfigurationElementBuilder.create().setName("child").setText("childText")) });
      executions = Collections.emptyList();
      dependencies = Collections.emptyList();
    }
  }

  public static class ExecutionHavingPlugin extends AbstractProfilePluginFacet {
    public ExecutionHavingPlugin() {
      pluginArtifact = DependencyArtifact.Clean;
      configurations = Collections.emptyList();
      dependencies = Collections.emptyList();

      final PluginExecution exec = new PluginExecution();
      exec.setId("testExec");
      exec.setGoals(Arrays.asList(new String[] { "compile" }));
      exec.setPhase("test");
      final MavenPluginAdapter adapter = new MavenPluginAdapter(MavenPluginBuilder.create().setDependency(
              DependencyBuilder.create("maven-clean-plugin")));
      adapter.setConfig(ConfigurationBuilder
              .create()
              .addConfigurationElement(
                      ConfigurationElementBuilder.create().setName("parent")
                              .addChild(ConfigurationElementBuilder.create().setName("child").setText("childText")))
              .addConfigurationElement(ConfigurationElementBuilder.create().setName("leaf").setText("leafText")));
      exec.setConfiguration(adapter.getConfiguration());
      executions = Arrays.asList(new PluginExecution[] { exec });
    }
  }

  @Test
  public void testEmptyPlugin() throws Exception {
    final Project project = initializeJavaProject();
    final DefinitionOnly facet = new DefinitionOnly();
    
    project.installFacet(facet);
    checkPlugin(project, facet, AbstractBaseFacet.MAIN_PROFILE);
  }
  
  @Test
  public void testDependencyHavingPlugin() throws Exception {
    final Project project = initializeJavaProject();
    final DependencyHavingPlugin facet = new DependencyHavingPlugin();
    
    project.installFacet(facet);
    checkPlugin(project, facet, AbstractBaseFacet.MAIN_PROFILE);
  }
  
  @Test
  public void testConfigHavingPlugin() throws Exception {
    final Project project = initializeJavaProject();
    final ConfigHavingPlugin facet = new ConfigHavingPlugin();
    
    project.installFacet(facet);
    checkPlugin(project, facet, AbstractBaseFacet.MAIN_PROFILE);
  }
  
  @Test
  public void testExecutionHavingPlugin() throws Exception {
    final Project project = initializeJavaProject();
    final ExecutionHavingPlugin facet = new ExecutionHavingPlugin();
    
    project.installFacet(facet);
    checkPlugin(project, facet, AbstractBaseFacet.MAIN_PROFILE);
  }
  
  protected void checkPlugin(Project project, AbstractProfilePluginFacet facet, String profileId) {
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    Profile profile = null;
    for (final Profile prof : coreFacet.getPOM().getProfiles()) {
      if (profileId.equals(prof.getId())) {
        profile = prof;
        break;
      }
    }
    Assert.notNull(profile, "Could not find profile with matching id, " + profileId);
    final BuildBase build = profile.getBuild();
    Assert.notNull(build, "No build for profile " + profileId);
    final Plugin plugin = build.getPluginsAsMap().get(facet.pluginArtifact.toString());
    final MavenPluginAdapter adapter = new MavenPluginAdapter(plugin);
    
    // This is hack to go from maven to forge configurations
    final MavenPluginAdapter configAdapter = new MavenPluginAdapter(MavenPluginBuilder.create().setDependency(DependencyBuilder.create("maven-clean-plugin")));
    
    final Collection<Execution> executions = new ArrayList<Execution>();
    for (final PluginExecution plugExec : facet.executions) {
      configAdapter.setConfiguration(plugExec.getConfiguration());
      ExecutionBuilder newExec = ExecutionBuilder.create().setId(plugExec.getId()).setPhase(plugExec.getPhase()).setConfig(configAdapter.getConfig());
      for (final String goal : plugExec.getGoals())
        newExec.addGoal(goal);
      executions.add(newExec);
    }
    checkExecutions(adapter, executions);
    checkDependencies(build, facet.dependencies, plugin.getDependencies(), facet.pluginArtifact.toString());
    checkConfigurations(adapter.getConfig(), facet.configurations);
  }

}
