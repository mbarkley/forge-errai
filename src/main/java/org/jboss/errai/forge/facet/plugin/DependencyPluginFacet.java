package org.jboss.errai.forge.facet.plugin;

import java.util.ArrayList;
import java.util.Arrays;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.facet.base.CoreBuildFacet;
import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ CoreBuildFacet.class })
public class DependencyPluginFacet extends AbstractPluginFacet {

  public DependencyPluginFacet() {
    pluginArtifact = DependencyArtifact.Dependency;
    configurations = new ArrayList<ConfigurationElement>(0);
    dependencies = new ArrayList<DependencyBuilder>(0);
    executions = Arrays.asList(new Execution[] {
            ExecutionBuilder.create().setId("unpack").setPhase("process-resources").addGoal("unpack")
            .setConfig(ConfigurationBuilder.create()
                    .addConfigurationElement(ConfigurationElementBuilder.create().setName("artifactItems")
                            .addChild(ConfigurationElementBuilder.create().setName("artifactItem")
                                    .addChild(ConfigurationElementBuilder.create()
                                            .setName("groupId").setText("org.jboss.as"))
                                    .addChild(ConfigurationElementBuilder.create()
                                            .setName("artifactId").setText("jboss-as-dist"))
                                    .addChild(ConfigurationElementBuilder.create()
                                            .setName("version").setText("7.1.1.Final"))
                                    .addChild(ConfigurationElementBuilder.create()
                                            .setName("type").setText("zip"))
                                    .addChild(ConfigurationElementBuilder.create()
                                            .setName("overWrite").setText("false"))
                                    .addChild(ConfigurationElementBuilder.create()
                                            .setName("outputDirectory").setText("${project.build.directory}"))
                                    
                             )
                    )
            )
    });
  }
  
}
