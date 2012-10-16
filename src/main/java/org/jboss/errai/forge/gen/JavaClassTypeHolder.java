package org.jboss.errai.forge.gen;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.java.Import;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;

public class JavaClassTypeHolder {

	String packageName;
	String javaClassName;
	List<Method<JavaClass>> methods;
	List<Method<JavaClass>> publicMethods;
	List<Import> imports;
	List<String> interfaces;
	List<String> publicMethodsSignaturesAsString;
	
	
	public JavaClassTypeHolder(String packageName, String typeName,
			List<Method<JavaClass>> methods, List<Import> imports,
			List<String> interfaces) {
		super();
		this.packageName = packageName;
		this.javaClassName = typeName;
		this.methods = methods;
		this.imports = imports;
		this.interfaces = interfaces;
		this.publicMethodsSignaturesAsString = new ArrayList<String>();
		
		for (Method<JavaClass> method : this.getPublicMethods()) {
			StringBuffer buf = new StringBuffer();
			buf.append(method.getVisibility());
			buf.append(" ");
			if(method.getReturnType().equalsIgnoreCase("null")) {
				buf.append("void");
			}
			else {
				buf.append(method.getReturnType());
			}
			buf.append(" ");
			buf.append(method.getName());
			buf.append(" (");
			if(method.getParameters() != null) {
					buf.append(method.getParameters().toString().substring(1, method.getParameters().toString().length() -1));
			}
			buf.append(")");
			this.publicMethodsSignaturesAsString.add(buf.toString());
		}
	}

	public String getPackageName() {
		return packageName;
	}

	public String getTypeName() {
		return javaClassName;
	}

	public List<Method<JavaClass>> getMethods() {
		return methods;
	}

	public List<Import> getImports() {
		return imports;
	}

	public List<String> getInterfaces() {
		return interfaces;
	}

	public List<Method<JavaClass>> getPublicMethods() {
		if(publicMethods == null){
			publicMethods = new ArrayList<Method<JavaClass>>();
			for(Method<JavaClass> method : this.methods){
				if(method.isPublic()) { 
					publicMethods.add(method);
				}
			}
		}
		return publicMethods;
	}

	public List<String> getPublicMethodsSignaturesAsString() {
		return publicMethodsSignaturesAsString;
	}
}
