package org.jboss.errai.forge;

import java.io.File;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.DirectoryResource;

public class Utils {
	
    public static final String replacePackageName(InputStream templateJavaFile, Project project) {
    	try {
    		final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
    		final String defaultJavaPackage = java.getBasePackage();
    		
            String templateJavaFileString =  new java.util.Scanner(templateJavaFile).useDelimiter("\\A").next();
            return templateJavaFileString.replace("${package}", defaultJavaPackage);
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }	

    public static final File getSourceFileForClass(Project project, String sourceClass) throws Exception{
		DirectoryResource root = project.getProjectRoot();
		root = root.getOrCreateChildDirectory("src").getOrCreateChildDirectory("main").getOrCreateChildDirectory("java");		
    	String rootPath = root.getUnderlyingResourceObject().getPath();
    	StringBuffer buf = new StringBuffer(rootPath);
    	StringTokenizer tokenizer = new StringTokenizer(sourceClass,".");
        while (tokenizer.hasMoreElements()) {
        	buf.append(System.getProperty("file.separator"));
			buf.append(tokenizer.nextToken().trim());
		}
        buf.append(".java");
        System.out.println("sourceFilePath: " + buf.toString());
        return new File(buf.toString());
    	
    }

}
