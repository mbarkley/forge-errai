package org.jboss.errai.forge.facet.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;

import org.apache.maven.model.BuildBase;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.PluginElement;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.test.AbstractShellTest;

public abstract class BasePluginFacetTest extends AbstractShellTest {

  protected void checkExecutions(MavenPlugin plugin, Collection<Execution> executions) {
    assertEquals(executions.size(), plugin.listExecutions().size());

    Outer: for (final Execution expected : executions) {
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
  protected void checkDependencies(BuildBase build, Collection<DependencyBuilder> expectedDependencies,
          Collection<org.apache.maven.model.Dependency> actualDependencies, String artifactKey) {
    assertEquals(expectedDependencies.size(), actualDependencies.size());

    Outer: for (final DependencyBuilder expected : expectedDependencies) {
      for (final org.apache.maven.model.Dependency outcome : actualDependencies) {
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

  protected void checkHasPlugin(Project project, AbstractPluginFacet facet, String artifactDef) {
    final MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
    final MavenPlugin plugin = pluginFacet.getPlugin(DependencyBuilder.create(artifactDef));
    assertNotNull(plugin);
  }

  protected void checkConfigurations(final Configuration config, Collection<ConfigurationElement> configurations) {
    assertEquals(configurations.size(), config.listConfigurationElements().size());

    for (final ConfigurationElement elem : configurations) {
      assertMatchingConfigElem(elem, config.getConfigurationElement(elem.getName()));
    }
  }

  protected void assertMatchingConfigElem(ConfigurationElement expected, ConfigurationElement outcome) {
    assertNotNull(outcome);
    assertEquals(expected.getChildren().size(), outcome.getChildren().size());

    if (expected.hasChilderen()) {
      for (final PluginElement raw : expected.getChildren()) {
        final ConfigurationElement expectedChild = ConfigurationElement.class.cast(raw);
        ConfigurationElement outcomeChild;
        int i;
        for (i = 0; i < outcome.getChildren().size(); i++) {
          outcomeChild = (ConfigurationElement) outcome.getChildren().get(i);
          try {
            assertMatchingConfigElem(expectedChild, outcomeChild);
            break;
          }
          catch (AssertionError e) {
            continue;
          }
        }
        if (i == outcome.getChildren().size()) {
          fail("Could not find match for " + expectedChild);
        }
      }
    }
    else {
      assertEquals(expected.getText(), outcome.getText());
    }
  }

}
