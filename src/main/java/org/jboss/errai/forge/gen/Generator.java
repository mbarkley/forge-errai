package org.jboss.errai.forge.gen;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.forge.Utils;
import org.jboss.errai.forge.enums.ErraiGeneratorCommandsEnum;
import org.jboss.errai.forge.enums.ResourcesEnum;
import org.jboss.errai.forge.template.Velocity;
import org.jboss.forge.project.Project;

public class Generator {
	
	private Project project;
	private Velocity velocity;
	
	public Generator(Project project, Velocity velocity) {
		this.project = project;
		this.velocity = velocity;
	}
	
	public void generate(ErraiGeneratorCommandsEnum command) {
		
		switch (command) {
		case ERRAI_BUS_GENERATE_REMOTES_FROM_ALL_SERVICE_CLASSES: 
			try {
				// find resources we want generate from
				SourceResolver sourceResolver = new SourceResolver(project);
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
		switch (command) {
		case ERRAI_BUS_GENERATE_REMOTE_FROM_SERVICE_CLASS: 
				try {
					// get resource we want generate from
					File file = Utils.getSourceFileForClass(project, source);
					// extract methods 
					this.generateRemoteFromService(file, command);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		case ERRAI_MARSHALING_SET_PORTABLE_RECURSIVE: 
			try {
				// find resources we want generate from
				SourceResolver sourceResolver = new SourceResolver(project);
				sourceResolver.searchForAllResources(source);
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
				File file = Utils.getSourceFileForClass(project, source);
				// extract methods 
				this.addPortable(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
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
			if(line.contains("public class")) {
				lineToAdd = sourceLines.indexOf(line) - 1;
			}
		}
		if(lineToAdd > 0){
			sourceLines.add(lineToAdd, "@Portable");
			builder.writeSource(sourceLines, file);
		}
	}
}
