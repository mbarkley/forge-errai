package org.jboss.errai.forge;

import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * @author pslegr
 */
@Alias("org.jboss.errai")
@RequiresFacet({ DependencyFacet.class, WebResourceFacet.class })
public class ErraiFacet extends BaseFacet
{
   static final String SUCCESS_MSG_FMT = "***SUCCESS*** %s %s has been installed.";
   static final String ALREADY_INSTALLED_MSG_FMT = "***INFO*** %s %s is already present.";

   @Inject
   private ShellPrompt prompt;
   @Inject
   private ShellPrintWriter writer;
   
	@Inject
	public DependencyInstaller installer;

	public boolean install() {
		//TODO use Errai-2.1 version for now, for the future count on new versions coming and use Shell prompt 
//		ErraiVersion version = prompt.promptChoiceTyped("Which version of Errai?",
//	               Arrays.asList(ErraiVersion.values()), ErraiVersion.ERRAI_2_1_0);
		installDependencies(ErraiVersion.ERRAI_2_1_0);
		installGWTPlugin(ErraiVersion.ERRAI_2_1_0);
		return true;
	}

	   @Override
	   public boolean isInstalled()
	   {
	      DependencyFacet deps = getProject().getFacet(DependencyFacet.class);
	      if (getProject().hasAllFacets(Arrays.asList(DependencyFacet.class, WebResourceFacet.class))) {
	         for (ErraiVersion version : ErraiVersion.values()) {
	            boolean hasVersionDependencies = true;
	            for (Dependency dependency : version.getDependencies()) {
	               if (!deps.hasEffectiveDependency(dependency)) {
	                  hasVersionDependencies = false;
	                  break;
	               }
	            }
	            if (hasVersionDependencies) {
	               return true;
	            }
	         }
	      }
	      return false;
	   }	
   /**
    * Install the maven dependencies required for Errai
    * 
    * @param version
    */
   private void installDependencies(final ErraiVersion version)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      for (Dependency dependency : version.getDependencies()) {
         deps.addDirectDependency(dependency);
      }
   }
   
   private void  installGWTPlugin(final ErraiVersion version) {
	   String gwtVersion = "";
	   for(Dependency dep : version.getDependencies()){
		   if(dep.getGroupId().startsWith("com.google.gwt")){
			   gwtVersion = dep.getVersion();
		   }
	   } 
	    
	    DependencyBuilder gwtDependencyBuilder = DependencyBuilder.create()
			    .setGroupId("org.codehaus.mojo")
			    .setArtifactId("gwt-maven-plugin")
			    .setVersion(gwtVersion);
			     
	    ExecutionBuilder execution = ExecutionBuilder.create()
	    		.addGoal("resources")
	    		.addGoal("compile");
	    MavenPluginBuilder gwtPlugin = MavenPluginBuilder.create()
	    		.setDependency(gwtDependencyBuilder)
			    .createConfiguration()
			    .createConfigurationElement("logLevel")
			    .setText("INFO")
			    .getParentPluginConfig().getOrigin()
			    .createConfiguration()
			    .createConfigurationElement("runTarget")
			    .setText("App.html")
			    .getParentPluginConfig().getOrigin()
			    .createConfiguration()
			    .createConfigurationElement("extraJvmArgs")
			    .setText("-Xmx512m")
			    .getParentPluginConfig().getOrigin()
			    .createConfiguration()
			    .createConfigurationElement("soyc")
			    .setText("false")
			    .getParentPluginConfig().getOrigin()
			    .createConfiguration()
			    .createConfigurationElement("hostedWebapp")
			    .setText("src/main/webapp/")
			    .getParentPluginConfig().getOrigin()
			    .createConfiguration()
			    .createConfigurationElement("treeLogger")
			    .setText("true")
			    .getParentPluginConfig().getOrigin()
			    .addExecution(execution);	    
	    
	    MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
	    pluginFacet.addPlugin(gwtPlugin);
	   
   }
	
}
