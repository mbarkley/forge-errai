package org.jboss.errai.forge.gen;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.forge.enums.ConfigEnum;
import org.jboss.errai.forge.enums.ErraiGeneratorCommandsEnum;
import org.jboss.errai.forge.enums.ResourcesEnum;
import org.jboss.errai.forge.template.Velocity;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;

public class Generator {
	
	private Project project;
	private Velocity velocity;
	
	public Generator(Project project, Velocity velocity) {
		this.project = project;
		this.velocity = velocity;
	}
	
	public void generate(ErraiGeneratorCommandsEnum command) {
		SourceResolver sourceResolver = new SourceResolver(project);
		
		switch (command) {
		case ERRAI_BUS_GENERATE_REMOTES_FROM_ALL_SERVICE_CLASSES: 
			try {
				sourceResolver.searchForAllResources(ResourcesEnum.SERVICE_REMOTE);
				List<File> resources = sourceResolver.getResources();
				// extract methods 
				for (File file : resources) {
					// extract methods 
					this.generateRemoteFromService(file, command);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	public void generate(ErraiGeneratorCommandsEnum command, String source) {
		SourceResolver sourceResolver = new SourceResolver(project);
		boolean recursive = false;
		
		switch (command) {
		case ERRAI_BUS_GENERATE_REMOTE_FROM_SERVICE_CLASS: 
				try {
					// get resource we want generate from
					File file = sourceResolver.getSourceFileForClass(source);
					// extract methods 
					this.generateRemoteFromService(file, command);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		case ERRAI_MARSHALING_SET_PORTABLE_RECURSIVE: 
			try {
				// find resources we want generate from
				sourceResolver.searchForAllResources(source, true);
				List<File> resources = sourceResolver.getResources();
				// extract methods 
				for (File file : resources) {
					this.addPortable(file);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		case ERRAI_MARSHALING_SET_PORTABLE: 
			try {
				// get resource we want generate from
				File file = sourceResolver.getSourceFileForClass(source);
				// extract methods 
				this.addPortable(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case ERRAI_MARSHALING_SET_PORTABLE_RECURSIVE_VIA_CONFIG:
			recursive = true;
		case ERRAI_MARSHALING_SET_PORTABLE_VIA_CONFIG: 
			try {
				// find resources we want generate from
				this.addPortableToConfig(sourceResolver.searchForAllResourcesFQN(source, recursive));
			} catch (Exception e) {
				e.printStackTrace();
			}
		case ERRAI_MARSHALING_IMMUTABLE_BUILDER_RECURSIVE:
			// TODO implement here
			break;
		case ERRAI_MARSHALING_IMMUTABLE_BUILDER:
			// TODO implement here
			break;
		}
	}

	
	private void generateRemoteFromService(File file, ErraiGeneratorCommandsEnum command) throws Exception{
		SourceParser sourceParser = new SourceParser();
		JavaClassTypeHolder jsth = sourceParser.parseJavaClass(file);
		
		// generate source
		Map<String, Object> contextParam = new HashMap<String, Object>();
		String typeName = jsth.getInterfaces().get(0);
		List<String> serviceMethods = jsth.getPublicMethodsSignaturesAsString();
		contextParam.put("className", typeName);
		contextParam.put("serviceMethods", serviceMethods);
		contextParam.put("imports", jsth.getImports());
		velocity.createJavaSource(command.getTemplateFQName(),contextParam, false);						
	}
	
	private void addPortable(File file) throws Exception{
		SourceBuilder builder = new SourceBuilder();
		List<String> sourceLines = builder.readSource(file);
		int lineToAdd = 0;
		for(String line : sourceLines) {
			// check if @Portable is not already there
			if(line.contains(ResourcesEnum.PORTABLE.name())){
				return;
			}
			if(line.contains("public class")) {
				lineToAdd = sourceLines.indexOf(line) - 1;
				return;
			}
		}
		if(lineToAdd > 0){
			sourceLines.add(lineToAdd, ResourcesEnum.PORTABLE.name());
			builder.writeSource(sourceLines, file);
		}
	}
	
	private void addPortableToConfig(List<String> javaTypes) throws Exception{
		if(javaTypes == null || javaTypes.size() == 0){
			System.out.println("Could not add serializable files into config the list is empty");
			return;
		} 
		DirectoryResource projectRoot = project.getProjectRoot();				
        DirectoryResource sourceRoot = projectRoot.getOrCreateChildDirectory("src").
        		getOrCreateChildDirectory("main").getOrCreateChildDirectory("resources");
        //get App props
        FileResource<?> appIndexPage = (FileResource<?>) sourceRoot.getChild("ErraiApp.properties");
        File props = appIndexPage.getUnderlyingResourceObject();
        System.out.println("props file: "+ props.getAbsolutePath());
        System.out.println("java types: "+ javaTypes);
		
		SourceBuilder builder = new SourceBuilder();
		List<String> sourceLines = builder.readSource(props);
		int lineToAdd = 0;
		for(String line : sourceLines) {
			if(line.contains(ConfigEnum.CONF_SERIALIZABLE.getKey())) {
				lineToAdd = sourceLines.indexOf(line);
				return;
			}
		}
		if(lineToAdd > 0){
			StringBuffer buf = new StringBuffer(sourceLines.get(lineToAdd));
			for (String javaType : javaTypes) {
				buf.append(" ");
				buf.append(javaType);
			}
			sourceLines.set(lineToAdd, buf.toString());
			builder.writeSource(sourceLines, props);
		}
		else {
			StringBuffer buf = new StringBuffer();
			buf.append(ConfigEnum.CONF_SERIALIZABLE.getKey() + "=");
			for (String javaType : javaTypes) {
				buf.append(" ");
				buf.append(javaType);
			}
			sourceLines.add(buf.toString());
			builder.writeSource(sourceLines, props);
		}
	}
	
}
