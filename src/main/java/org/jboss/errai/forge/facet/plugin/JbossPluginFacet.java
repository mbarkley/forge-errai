package org.jboss.errai.forge.facet.plugin;

import java.util.ArrayList;

import org.apache.maven.model.PluginExecution;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.errai.forge.facet.base.CoreBuildFacet;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * This facet configures the jboss-as-maven-plugin in the
 * {@link AbstractBaseFacet#MAIN_PROFILE main profile} of the pom file.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ CoreBuildFacet.class })
public class JbossPluginFacet extends AbstractProfilePluginFacet {

  public JbossPluginFacet() {
    pluginArtifact = DependencyArtifact.JbossPlugin;
    dependencies = new ArrayList<DependencyBuilder>(0);
    executions = new ArrayList<PluginExecution>(0);
    configurations = new ArrayList<ConfigurationElement>(0);
    extensions = false;
  }
}
