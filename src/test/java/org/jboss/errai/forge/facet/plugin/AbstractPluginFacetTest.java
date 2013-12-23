package org.jboss.errai.forge.facet.plugin;

import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.model.Build;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.junit.Test;

public class AbstractPluginFacetTest extends BasePluginFacetTest {

  public static class DefinitionOnly extends AbstractPluginFacet {
    public DefinitionOnly() {
      pluginArtifact = DependencyArtifact.Clean;
      configurations = Collections.emptyList();
      dependencies = Collections.emptyList();
      executions = Collections.emptyList();
    }
  }

  public static class DependencyHavingPlugin extends AbstractPluginFacet {
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

  public static class ConfigHavingPlugin extends AbstractPluginFacet {
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

  public static class ExecutionHavingPlugin extends AbstractPluginFacet {
    public ExecutionHavingPlugin() {
      pluginArtifact = DependencyArtifact.Clean;
      configurations = Collections.emptyList();
      executions = Arrays.asList(new Execution[] { ExecutionBuilder
              .create()
              .setId("testExec")
              .setPhase("compile")
              .setConfig(
                      ConfigurationBuilder
                              .create()
                              .addConfigurationElement(
                                      ConfigurationElementBuilder
                                              .create()
                                              .setName("parent")
                                              .addChild(
                                                      ConfigurationElementBuilder.create().setName("child")
                                                              .setText("childText")))
                              .addConfigurationElement(
                                      ConfigurationElementBuilder.create().setName("leaf").setText("leafText"))) });
      dependencies = Collections.emptyList();
    }
  }

  @Test
  public void testEmptyPluginDefinition() throws Exception {
    final Project project = initializeJavaProject();
    final DefinitionOnly facet = new DefinitionOnly();

    project.installFacet(facet);
    checkPlugin(project, facet);
  }

  @Test
  public void testWithDependencies() throws Exception {
    final Project project = initializeJavaProject();
    final DependencyHavingPlugin facet = new DependencyHavingPlugin();

    project.installFacet(facet);
    checkPlugin(project, facet);
  }

  @Test
  public void testWithConfigurations() throws Exception {
    final Project project = initializeJavaProject();
    final ConfigHavingPlugin facet = new ConfigHavingPlugin();

    project.installFacet(facet);
    checkPlugin(project, facet);
  }

  @Test
  public void testWithExecutions() throws Exception {
    final Project project = initializeJavaProject();
    final ExecutionHavingPlugin facet = new ExecutionHavingPlugin();

    project.installFacet(facet);
    checkPlugin(project, facet);
  }

  private void checkPlugin(Project project, AbstractPluginFacet facet) {
    final String artifactDef = facet.pluginArtifact.toString();
    final MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    checkHasPlugin(project, facet, artifactDef);
    checkExecutions(pluginFacet.getPlugin(DependencyBuilder.create(artifactDef)), facet.executions);
    Build build = coreFacet.getPOM().getBuild();
    if (build == null)
      build = new Build();
    checkDependencies(build, facet.dependencies, build.getPluginsAsMap().get(facet.pluginArtifact.toString())
            .getDependencies(), facet.pluginArtifact.toString());
    checkConfigurations(pluginFacet.getPlugin(DependencyBuilder.create(artifactDef)).getConfig(), facet.configurations);
  }

}
