package org.jboss.errai.forge.facet;

import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;

public class ErraiGWTPlugin {
	
	private String gwtPluginVersion;
	private DependencyBuilder gwtDependencyBuilder;
	private ExecutionBuilder execution;
	private MavenPluginBuilder gwtPlugin;
	
	public ErraiGWTPlugin() {
		    
		  String gwtVersion= Versions.getInstance().getGwt_version();
		
		    gwtDependencyBuilder = DependencyBuilder.create()
				    .setGroupId("org.codehaus.mojo")
				    .setArtifactId("gwt-maven-plugin")
				    .setVersion(gwtVersion);
				     
		    execution = ExecutionBuilder.create()
		    		.addGoal("resources")
		    		.addGoal("compile");
		    gwtPlugin = MavenPluginBuilder.create()
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
		   
	}

	public String getGwtPluginVersion() {
		return gwtPluginVersion;
	}

	public void setGwtPluginVersion(String gwtPluginVersion) {
		this.gwtPluginVersion = gwtPluginVersion;
	}

	public DependencyBuilder getGwtDependencyBuilder() {
		return gwtDependencyBuilder;
	}

	public void setGwtDependencyBuilder(DependencyBuilder gwtDependencyBuilder) {
		this.gwtDependencyBuilder = gwtDependencyBuilder;
	}

	public ExecutionBuilder getExecution() {
		return execution;
	}

	public void setExecution(ExecutionBuilder execution) {
		this.execution = execution;
	}

	public MavenPluginBuilder getGwtPlugin() {
		return gwtPlugin;
	}

	public void setGwtPlugin(MavenPluginBuilder gwtPlugin) {
		this.gwtPlugin = gwtPlugin;
	}

}
