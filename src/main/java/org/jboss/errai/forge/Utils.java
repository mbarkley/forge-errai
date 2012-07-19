package org.jboss.errai.forge;

import java.io.InputStream;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;

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


}
