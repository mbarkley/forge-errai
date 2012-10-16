package org.jboss.errai.forge.template;

import junit.framework.TestCase;

public class VelocityTest  extends TestCase{
	
	VelocityUtil velocityUtil;
	 @Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		velocityUtil = new VelocityUtil();
	}
	 
	 
//	public void testExtractRemoteFromService(){
//		File file = new File("src/main/java/org/jboss/errai/forge/template/Velocity.java");
//		assertTrue(file.isFile());
//		FileInputStream in=null;		
//        CompilationUnit cu;
//        try {
//    		// creates an input stream for the file to be parsed
//            in = new FileInputStream(file);
//            
//            // parse the file
//            cu = JavaParser.parse(in);
//            
//            // visit and print the methods names
//            new MethodVisitor().visit(cu, null);
//            
//        } catch(ParseException e){
//        	e.printStackTrace();
//        } catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//        finally {
//            try {
//				in.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }
//
//    }
//
//    /**
//     * Simple visitor implementation for visiting MethodDeclaration nodes. 
//     */
//    private static class MethodVisitor extends VoidVisitorAdapter<Object> {
//
//        @Override
//        public void visit(MethodDeclaration n, Object arg) {
//            // here you can access the attributes of the method.
//            // this method will be called for all methods in this 
//            // CompilationUnit, including inner class methods
//            System.out.println(n.getName());
//        }
//		
//	}
	
}
