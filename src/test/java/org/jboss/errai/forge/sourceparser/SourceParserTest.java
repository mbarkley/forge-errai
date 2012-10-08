package org.jboss.errai.forge.sourceparser;

import java.io.File;
import java.lang.reflect.Modifier;

import org.jboss.errai.forge.gen.Method;
import org.jboss.errai.forge.gen.SourceParser;

import junit.framework.TestCase;

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
			sourceParser.parse(file);
			assertNotNull(sourceParser.getMethods());
			assertEquals(14,sourceParser.getMethods().size());
			assertEquals(9,sourceParser.getPublicMethods().size());
			System.out.println("--- PUBLIC METHODS ---");
			for (Method method : sourceParser.getPublicMethods()) {
				String name = method.getName();
				System.out.println("METHOD: "+ 
						Modifier.toString(method.getModifiers())+" "+
						method.getType()+" "+ 
						method.getName()+"("+
						method.getParametersAsString()+")");
				//System.out.println(method);				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
