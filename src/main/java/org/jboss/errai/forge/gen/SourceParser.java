package org.jboss.errai.forge.gen;

import java.io.File;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;

public class SourceParser {
	
//	List<Method> methods = new ArrayList<Method>();
//	List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
//	List<TypeDeclaration> types = new ArrayList<TypeDeclaration>();
//	List<String> interfaces = new ArrayList<String>();
//	String classname;
//	List<AnnotationExpr> typeAnnotations = new ArrayList<AnnotationExpr>(); 

    public JavaClassTypeHolder parseJavaClass(File file) throws Exception {
    	JavaClass js = JavaParser.parse(JavaClass.class,file);
    	return new JavaClassTypeHolder(js.getPackage(), js.getName(), js.getMethods(), js.getImports(), js.getInterfaces());
    }
	
	
//    public CompilationUnit parse(File file) throws Exception {
//        // creates an input stream for the file to be parsed
//        FileInputStream in = new FileInputStream(file);
//
//        CompilationUnit cu;
//        try {
//            // parse the file
//            cu = JavaParser.parse(in);
//            imports = cu.getImports();
//            types = cu.getTypes();
//            classname = types.get(0).getName();
//            // extract interfaces from types
//            TypeDeclaration type = types.get(0);
//            typeAnnotations = type.getAnnotations();
//            String typeStr = type.toString();
//            String identf = "class " + classname + " implements ";
//            if(typeStr.contains(identf)) {
//                int index = typeStr.lastIndexOf(identf) + identf.length();
//                int lastIndex = typeStr.indexOf("{");
//                String intf = typeStr.substring(index,lastIndex);
//                StringTokenizer tokenizer = new StringTokenizer(intf,",");
//                while (tokenizer.hasMoreElements()) {
//    				interfaces.add(tokenizer.nextToken().trim());
//    			}
//            }
//            //System.out.println("intf:" + interfaces);
//            System.out.println("imports:" + imports);
//        } finally {
//            in.close();
//        }
//
//        // visit and print the methods names
//        new MethodVisitor().visit(cu, null);
//        return cu;
//    }
//
//    /**
//     * Simple visitor implementation for visiting MethodDeclaration nodes. 
//     */
//    class MethodVisitor extends VoidVisitorAdapter<Object> {
//
//        @Override
//        public void visit(MethodDeclaration n, Object arg) {
//            // here you can access the attributes of the method.
//            // this method will be called for all methods in this 
//            // CompilationUnit, including inner class methods
//        	
//        	methods.add(new Method(n.getAnnotations(), n.getBeginLine(), n.getEndLine(),
//        			n.getBody(),n.getModifiers(), n.getName(), n.getParameters(), n.getType(),
//        			n.getThrows()));
//        	if(Modifier.isPublic(n.getModifiers())) {
//            	publicMethods.add(new Method(n.getAnnotations(), n.getBeginLine(), n.getEndLine(),
//            			n.getBody(),n.getModifiers(), n.getName(), n.getParameters(), n.getType(),
//            			n.getThrows()));
//        	}
//        }
//    }

//	public List<Method> getMethods() {
//		return methods;
//	}
//
//	public void setMethods(List<Method> methods) {
//		this.methods = methods;
//	}
//
//	public List<ErraiTypeHolder> getPublicMethods() {
//		List<ErraiTypeHolder> publicMethods = new ArrayList<ErraiTypeHolder>();
//		return publicMethods;
//	}
//
//	public void setPublicMethods(List<Method> publicMethods) {
//		this.publicMethods = publicMethods;
//	}
//
//	public List<ImportDeclaration> getImports() {
//		return imports;
//	}
//
//	public List<TypeDeclaration> getTypes() {
//		return types;
//	}
//
//	public List<String> getInterfaces() {
//		return interfaces;
//	}
//
//	public String getClassname() {
//		return classname;
//	}
//
//	public List<AnnotationExpr> getTypeAnnotations() {
//		return typeAnnotations;
//	}
}