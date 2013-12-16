package org.jboss.errai.forge.facet.plugin;

import java.util.ArrayList;
import java.util.Arrays;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.project.dependencies.DependencyBuilder;


public class CleanPluginFacet extends AbstractPluginFacet {

  public CleanPluginFacet() {
    pluginArtifact = DependencyArtifact.Clean;
    dependencies = new ArrayList<DependencyBuilder>(0);
    executions = new ArrayList<Execution>(0);
    
    configurations = Arrays.asList(new ConfigurationElement[] {
            ConfigurationElementBuilder.create().setName("filesets").addChild(
                    ConfigurationElementBuilder.create().setName("fileset")
                    .addChild(ConfigurationElementBuilder.create().setName("directory").setText("${basedir}"))
                    .addChild(ConfigurationElementBuilder.create().setName("includes")
                            .addChild(ConfigurationElementBuilder.create().setName("include"))
                            .setText("src/main/webapp/app/")
                            .addChild(ConfigurationElementBuilder.create().setName("include"))
                            .setText("src/main/webapp/WEB-INF/deploy/")
                            .addChild(ConfigurationElementBuilder.create().setName("include"))
                            .setText("src/main/webapp/WEB-INF/lib/")
                            .addChild(ConfigurationElementBuilder.create().setName("include"))
                            .setText("**/gwt-unitCache/**")
                            .addChild(ConfigurationElementBuilder.create().setName("include"))
                            .setText(".errai/")
                    )
            )
    });
  }
  
}
