package org.jboss.errai.forge.facet.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.ConfigurationElementNotFoundException;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.PluginElement;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class AbstractPluginFacetTest extends AbstractShellTest {

  public abstract static class BaseTestImpl extends AbstractPluginFacet {

  }

  public static class DefinitionOnly extends BaseTestImpl {
    public DefinitionOnly() {
      pluginArtifact = DependencyArtifact.Clean;
      configurations = Collections.emptyList();
      dependencies = Collections.emptyList();
      executions = Collections.emptyList();
    }
  }

  public static class DependencyHavingPlugin extends BaseTestImpl {
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

  public static class ConfigHavingPlugin extends BaseTestImpl {
    public ConfigHavingPlugin() {
      pluginArtifact = DependencyArtifact.Clean;
      configurations = Arrays.asList(new ConfigurationElement[] {
              ConfigurationElementBuilder.create().setName("configName").setText("configText"),
              ConfigurationElementBuilder.create().setName("parent").addChild(
                      ConfigurationElementBuilder.create().setName("child").setText("childText")
              )
      });
      executions = Collections.emptyList();
      dependencies = Collections.emptyList();
    }
  }

  public static class ExecutionHavingPlugin extends BaseTestImpl {
    public ExecutionHavingPlugin() {
      pluginArtifact = DependencyArtifact.Clean;
      configurations = Collections.emptyList();
      executions = Arrays.asList(new Execution[] {
              ExecutionBuilder.create().setId("testExec").setPhase("compile").setConfig(
                      ConfigurationBuilder.create().addConfigurationElement(
                              ConfigurationElementBuilder.create().setName("parent")
                              .addChild(ConfigurationElementBuilder.create().setName("child").setText("childText"))
                      )
                      .addConfigurationElement(ConfigurationElementBuilder.create().setName("leaf").setText("leafText"))
              )
      });
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

  protected void checkPlugin(Project project, BaseTestImpl facet) {
    final String artifactDef = facet.pluginArtifact.toString();
    checkHasPlugin(project, facet, artifactDef);
    checkExecutions(project, facet, artifactDef);
    checkDependencies(project, facet, artifactDef);
    checkConfigurations(project, facet, artifactDef);
  }

  protected void checkExecutions(Project project, BaseTestImpl facet, String artifactDef) {
    final MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
    final MavenPlugin plugin = pluginFacet.getPlugin(DependencyBuilder.create(artifactDef));

    assertEquals(facet.executions.size(), plugin.listExecutions().size());

    Outer: for (final Execution expected : facet.executions) {
      for (final Execution outcome : plugin.listExecutions()) {
        if (expected.getId().equals(outcome.getId())) {
          assertEquals(expected.getPhase(), outcome.getPhase());
          assertEquals(new HashSet<String>(expected.getGoals()), new HashSet<String>(outcome.getGoals()));
          assertEquals(expected.getConfig().listConfigurationElements().size(), outcome.getConfig()
                  .listConfigurationElements().size());
          for (final ConfigurationElement expConfigElem : expected.getConfig().listConfigurationElements()) {
            assertMatchingConfigElem(expConfigElem, outcome.getConfig()
                    .getConfigurationElement(expConfigElem.getName()));
          }
          continue Outer;
        }
      }
      fail("Execution with id " + expected.getId() + " was not in plugin.");
    }
  }

  // TODO check dependency exclusions
  protected void checkDependencies(Project project, BaseTestImpl facet, String artifactDef) {
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();
    final Build build = (pom.getBuild() != null) ? pom.getBuild() : new Build();
    final Plugin corePlugin = build.getPluginsAsMap().get(facet.pluginArtifact.toString());
    
    assertEquals(facet.dependencies.size(), corePlugin.getDependencies().size());

    Outer: for (final DependencyBuilder expected : facet.dependencies) {
      for (final org.apache.maven.model.Dependency outcome : corePlugin.getDependencies()) {
        if (expected.getGroupId().equals(outcome.getGroupId())
                && expected.getArtifactId().equals(outcome.getArtifactId())) {
          assertEquals(expected.getGroupId(), outcome.getGroupId());
          assertEquals(expected.getArtifactId(), outcome.getArtifactId());
          assertEquals((expected.getPackagingType() != null) ? expected.getPackagingType() : "jar", outcome.getType());
          assertEquals(expected.getScopeType(), outcome.getScope());
          if (ScopeType.SYSTEM.equals(expected.getScopeTypeEnum()))
            assertEquals(expected.getSystemPath(), outcome.getSystemPath());
          
          continue Outer;
        }
      }
      fail(expected.toString() + " artifact was not added to dependencies.");
    }
  }

  protected void checkHasPlugin(Project project, BaseTestImpl facet, String artifactDef) {
    final MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
    final MavenPlugin plugin = pluginFacet.getPlugin(DependencyBuilder.create(artifactDef));
    assertNotNull(plugin);
  }

  protected void checkConfigurations(Project project, BaseTestImpl facet, String artifactDef) {
    final MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
    final MavenPlugin plugin = pluginFacet.getPlugin(DependencyBuilder.create(artifactDef));
    final Configuration config = plugin.getConfig();

    assertEquals(facet.configurations.size(), config.listConfigurationElements().size());

    for (final ConfigurationElement elem : facet.configurations) {
      assertMatchingConfigElem(elem, config.getConfigurationElement(elem.getName()));
    }
  }

  private void assertMatchingConfigElem(ConfigurationElement expected, ConfigurationElement outcome) {
    assertNotNull(outcome);
    assertEquals(expected.getChildren().size(), outcome.getChildren().size());

    if (expected.hasChilderen()) {
      for (final PluginElement raw : expected.getChildren()) {
        final ConfigurationElement expectedChild = ConfigurationElement.class.cast(raw);
        try {
          final ConfigurationElement outcomeChild = outcome.getChildByName(expectedChild.getName(), true);
          assertMatchingConfigElem(expectedChild, outcomeChild);
        }
        catch (ConfigurationElementNotFoundException e) {
          fail("Could not find expected configuration element: " + expected.toString());
        }
      }
    }
    else {
      assertEquals(expected.getText(), outcome.getText());
    }
  }

}
