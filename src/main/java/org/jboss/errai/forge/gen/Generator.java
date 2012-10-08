package org.jboss.errai.forge.gen;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.forge.Utils;
import org.jboss.errai.forge.facet.ErraiGeneratorCommandsEnum;
import org.jboss.errai.forge.template.ResourcesEnum;
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
				List<File> resources = new SourceResolver(project,ResourcesEnum.SERVICE_REMOTE).getResources();
				// extract methods 
				for (File file : resources) {
					this.generateRemoteFromService(file, command);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	public void generate(ErraiGeneratorCommandsEnum command, String sourceClass) {
		switch (command) {
		case ERRAI_BUS_GENERATE_REMOTE_FROM_SERVICE_CLASS: 
				try {
					// get resource we want generate from
					File file = Utils.getSourceFileForClass(project, sourceClass);
					// extract methods 
					this.generateRemoteFromService(file, command);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}
		
	}

	
	private void generateRemoteFromService(File file, ErraiGeneratorCommandsEnum command) throws Exception{
		SourceParser sourceParser = new SourceParser();
		sourceParser.parse(file);
		
		// generate source
		List<Method> serviceMethods = sourceParser.getPublicMethods();
		Map<String, Object> contextParam = new HashMap<String, Object>();
		String serviceName = sourceParser.interfaces.get(0);
		contextParam.put("className", serviceName);
		contextParam.put("serviceMethods", serviceMethods);
		contextParam.put("imports", sourceParser.imports);
		velocity.createJavaSource(command.getTemplateFQName(),contextParam, false);						
		
		
		// print.out for methods
//		System.out.println("--- PUBLIC METHODS ---");
//		for (Method serviceMethod : sourceParser.getPublicMethods()) {
//			String name = serviceMethod.getName();
//			System.out.println("METHOD: "+ 
//					Modifier.toString(serviceMethod.getModifiers())+" "+
//					serviceMethod.getType()+" "+ 
//					serviceMethod.getName()+"("+
//					serviceMethod.getParametersAsString()+")");
//		}
		
		
	}

}
