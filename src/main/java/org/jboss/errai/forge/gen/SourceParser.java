package org.jboss.errai.forge.gen;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class SourceParser {
	
	List<Method> methods = new ArrayList<Method>();
	List<Method> publicMethods = new ArrayList<Method>();
	List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
	List<TypeDeclaration> types = new ArrayList<TypeDeclaration>();
	List<String> interfaces = new ArrayList<String>();
	String classname;
	
	
    public void parse(File file) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(file);

        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
            imports = cu.getImports();
            types = cu.getTypes();
            classname = types.get(0).getName();
            // extract interfaces from types
            TypeDeclaration type = types.get(0);
            String typeStr = type.toString();
            String identf = "class " + classname + " implements ";
            if(typeStr.contains(identf)) {
                int index = typeStr.lastIndexOf(identf) + identf.length();
                int lastIndex = typeStr.indexOf("{");
                String intf = typeStr.substring(index,lastIndex);
                StringTokenizer tokenizer = new StringTokenizer(intf,",");
                while (tokenizer.hasMoreElements()) {
    				interfaces.add(tokenizer.nextToken().trim());
    			}
            }
            //System.out.println("intf:" + interfaces);
            System.out.println("imports:" + imports);
        } finally {
            in.close();
        }

        // visit and print the methods names
        new MethodVisitor().visit(cu, null);
    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes. 
     */
    class MethodVisitor extends VoidVisitorAdapter<Object> {

        @Override
        public void visit(MethodDeclaration n, Object arg) {
            // here you can access the attributes of the method.
            // this method will be called for all methods in this 
            // CompilationUnit, including inner class methods
        	
        	methods.add(new Method(n.getAnnotations(), n.getBeginLine(), n.getEndLine(),
        			n.getBody(),n.getModifiers(), n.getName(), n.getParameters(), n.getType(),
        			n.getThrows()));
        	if(Modifier.isPublic(n.getModifiers())) {
            	publicMethods.add(new Method(n.getAnnotations(), n.getBeginLine(), n.getEndLine(),
            			n.getBody(),n.getModifiers(), n.getName(), n.getParameters(), n.getType(),
            			n.getThrows()));
        	}
        }
    }

	public List<Method> getMethods() {
		return methods;
	}

	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

	public List<Method> getPublicMethods() {
		return publicMethods;
	}

	public void setPublicMethods(List<Method> publicMethods) {
		this.publicMethods = publicMethods;
	}

	public List<ImportDeclaration> getImports() {
		return imports;
	}

	public List<TypeDeclaration> getTypes() {
		return types;
	}

	public List<String> getInterfaces() {
		return interfaces;
	}

	public String getClassname() {
		return classname;
	}
}