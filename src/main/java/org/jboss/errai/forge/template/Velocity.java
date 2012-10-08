package org.jboss.errai.forge.template;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jboss.errai.forge.facet.ErraiBaseFacet;
import org.jboss.errai.forge.gen.Method;
import org.jboss.errai.forge.gen.SourceParser;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaType;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.java.JavaResource;

public class Velocity {
	
	Project project;
	
	private static final String UTF_8 = "UTF-8";
	private final VelocityEngine velocityEngine;
	private static final VelocityUtil VELOCITY_UTIL = new VelocityUtil();
	
	
	public final static String TEMPLATE_FILES="code-templates/files/";
	public final static String TEMPLATE_TEMPLATES="code-templates/templates/";
	public final static String TEMPLATE_FILES_SUFF=".java.vm";
	public final static String TEMPLATE_TEMPLATES_SUFF=".vm";
	
	List<File> resources;
	String searchString;
    
	
	
	public Velocity(Project project) {
		this.project = project;
		
        velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER,
				"classpath");
		velocityEngine.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		velocityEngine.setProperty(
				RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"org.apache.velocity.runtime.log.JdkLogChute");        
		
	}
	
	private VelocityContext createVelocityContext(
			Map<String, Object> parameter) {
		if (parameter == null) {
			parameter = new HashMap<String, Object>();
		}
		JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
		ErraiBaseFacet erraiFacet = project.getFacet(ErraiBaseFacet.class);
		VelocityContext velocityContext = new VelocityContext(parameter);

		String classPrefix = getClassPrefix();
		velocityContext.put("classPrefix", classPrefix);
		velocityContext.put("basePackage", java.getBasePackage());
		velocityContext.put("errai", erraiFacet);
		velocityContext.put("java", java);
		velocityContext.put("util", VELOCITY_UTIL);
		return velocityContext;
	}
	
	public void createJavaSource(String template, boolean hasTemplateName) {
		createJavaSource(template, new HashMap<String, Object>(), hasTemplateName);
	}
	
	public void createJavaSourceWithTemplateName(String template) {
		createJavaSource(template, new HashMap<String, Object>(),true);
	}
	
	
	public JavaResource createJavaSource(String template,
			Map<String, Object> parameter, boolean hasTemplateName) {
		JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
		VelocityContext velocityContext = createVelocityContext(parameter);
		
		StringWriter stringWriter = new StringWriter();
		velocityEngine.mergeTemplate(this.adjustTemplatePath(template), UTF_8, velocityContext,
				stringWriter);

		JavaType<?> serviceClass = JavaParser.parse(JavaType.class,
				stringWriter.toString());

		if(hasTemplateName) {
			String className = getClassNameFromTemplateName(template);
			serviceClass.setName(className);
		}
		else {
			String className = (String)velocityContext.get("className");
			if(className == null) {
				className = "ClassName";
			}
			serviceClass.setName(className);
		}
		try {
			JavaResource saveJavaSource = java.saveJavaSource(serviceClass);
			return saveJavaSource;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JavaResource extractJavaSourceFromTemplate(String template,
			Map<String, Object> parameter ) throws Exception {
		JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
		VelocityContext velocityContext = createVelocityContext(parameter);
		
		
		// extract methods 
		for (File file : resources) {
			SourceParser sourceParser = new SourceParser();
			sourceParser.parse(file);
			System.out.println("--- PUBLIC METHODS ---");
			for (Method method : sourceParser.getPublicMethods()) {
				String name = method.getName();
				System.out.println("METHOD: "+ 
						Modifier.toString(method.getModifiers())+" "+
						method.getType()+" "+ 
						method.getName()+"("+
						method.getParameters()+")");
				//System.out.println(method);				
			}
		}
		
		
		
		
		StringWriter stringWriter = new StringWriter();
		velocityEngine.mergeTemplate(this.adjustTemplatePath(template), UTF_8, velocityContext,
				stringWriter);

		JavaType<?> serviceClass = JavaParser.parse(JavaType.class,
				stringWriter.toString());
		
		String className = getClassNameFromTemplateName(template);
		serviceClass.setName(className);
		try {
			JavaResource saveJavaSource = java.saveJavaSource(serviceClass);
			return saveJavaSource;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String adjustTemplatePath(String template) {
		if(template.endsWith(Velocity.TEMPLATE_FILES_SUFF))
			return Velocity.TEMPLATE_FILES + template;
		
		if(template.endsWith(Velocity.TEMPLATE_TEMPLATES_SUFF))
			return Velocity.TEMPLATE_TEMPLATES + template;
		
		return template;
		
	}
	
	private String getClassPrefix() {
		String artifactId = getSaveProjectName();
		return StringUtils.capitalize(artifactId);
	}
	
	public String getSaveProjectName() {
		final JavaSourceFacet javaSourceFacet = project
				.getFacet(JavaSourceFacet.class);
		String basePackage = javaSourceFacet.getBasePackage();
		
		return StringUtils.substring(basePackage,
				StringUtils.lastIndexOf(basePackage, ".") + 1);
	}
	
	public String getClassNameFromTemplateName(String templateName) {
		return StringUtils.substringBefore(templateName,".");		
	}
	
}
