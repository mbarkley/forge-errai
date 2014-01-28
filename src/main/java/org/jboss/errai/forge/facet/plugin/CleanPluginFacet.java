package org.jboss.errai.forge.facet.plugin;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.ProjectConfigFactory;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.facet.base.CoreBuildFacet;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.plugins.ConfigurationElement;
import org.jboss.forge.addon.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.addon.maven.plugins.Execution;

/**
 * This facet configures the maven-clean-plugin in the build section of the
 * projects pom file.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@FacetConstraint({ CoreBuildFacet.class })
public class CleanPluginFacet extends AbstractPluginFacet {

  private @Inject
  ProjectConfigFactory factory;

  public CleanPluginFacet() {
    pluginArtifact = DependencyArtifact.Clean;
    dependencies = new ArrayList<DependencyBuilder>(0);
    executions = new ArrayList<Execution>(0);

    configurations = Arrays.asList(new ConfigurationElement[] {
        ConfigurationElementBuilder.create().setName("filesets").addChild(
                ConfigurationElementBuilder.create().setName("fileset")
                        .addChild(ConfigurationElementBuilder.create().setName("directory").setText("${basedir}"))
                        .addChild(ConfigurationElementBuilder.create().setName("includes")
                                .addChild(ConfigurationElementBuilder.create().setName("include")
                                        .setText("src/main/webapp/WEB-INF/deploy/"))
                                .addChild(ConfigurationElementBuilder.create().setName("include")
                                        .setText("src/main/webapp/WEB-INF/lib/"))
                                .addChild(ConfigurationElementBuilder.create().setName("include")
                                        .setText("**/gwt-unitCache/**"))
                                .addChild(ConfigurationElementBuilder.create().setName("include")
                                        .setText(".errai/"))
                        )
                )
    });
  }

  @PostConstruct
  public void init() {
    final String moduleName = factory.getProjectConfig(getProject()).getProjectProperty(
            ProjectProperty.MODULE_NAME,
            String.class);
    ((ConfigurationElementBuilder) configurations.iterator().next().getChildByName("includes"))
            .addChild(ConfigurationElementBuilder.create().setName("include")
                    .setText("src/main/webapp/" + moduleName + "/"));
  }
}
