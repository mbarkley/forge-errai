package org.jboss.errai.forge.facet.plugin;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.maven.model.PluginExecution;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.facet.base.RequiresCore;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;

@RequiresCore
public class WarPluginFacet extends AbstractProfilePluginFacet {

  public WarPluginFacet() {
    pluginArtifact = DependencyArtifact.War;
    dependencies = new ArrayList<DependencyBuilder>(0);
    executions = new ArrayList<PluginExecution>(0);
    configurations = Arrays.asList(new ConfigurationElement[] {
            ConfigurationElementBuilder.create().setName("packagingExcludes")
            .setText("**/javax/**/*.*,**/client/local/**/*.class"),
            ConfigurationElementBuilder.create().setName("outputFileNameMapping")
            .setText("@{artifactId}@-@{baseVersion}@@{dashClassifier?}@.@{extension}@"),
    });
  }
}
