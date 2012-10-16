package org.jboss.errai.forge.gen;

import java.io.File;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;

public class SourceParser {
	
    public JavaClassTypeHolder parseJavaClass(File file) throws Exception {
    	JavaClass js = JavaParser.parse(JavaClass.class,file);
    	return new JavaClassTypeHolder(js.getPackage(), js.getName(), js.getMethods(), js.getImports(), js.getInterfaces());
    }
}