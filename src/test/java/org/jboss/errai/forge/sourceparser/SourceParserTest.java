package org.jboss.errai.forge.sourceparser;

import java.io.File;

import junit.framework.TestCase;

import org.jboss.errai.forge.gen.JavaClassTypeHolder;
import org.jboss.errai.forge.gen.SourceParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;

public class SourceParserTest  extends TestCase{
	
	SourceParser sourceParser;
	 @Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		sourceParser = new SourceParser();
	}
	 
	public void testMethod(){
		File file = new File("src/test/java/org/jboss/errai/forge/sourceparser/SimpleServiceImpl.java.txt");
		try {
			JavaClassTypeHolder jcth = sourceParser.parseJavaClass(file);
			assertNotNull(jcth.getMethods());
			assertEquals(4,jcth.getMethods().size());
			assertEquals(3,jcth.getPublicMethods().size());
			System.out.println("--- PUBLIC METHODS ---");
			for (Method<JavaClass> method : jcth.getPublicMethods()) {
				String name = method.getName();
				System.out.println("METHOD: "+ 
						method.getVisibility()+" "+
						method.getReturnType()+" "+ 
						method.getName()+"("+
						method.getParameters()+")");
				//System.out.println(method);				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
