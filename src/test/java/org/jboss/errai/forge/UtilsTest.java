package org.jboss.errai.forge;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
 
public class UtilsTest extends AbstractShellTest
{
	
	private Project project;
	
   // Notice that you may use injection to do verification of the internal state of the system.
   // Or to perform additional operations.
   @Inject
   private DependencyResolver resolver;
 
   @Deployment
   public static JavaArchive getDeployment()
   {
      // The deployment method is where you must add references to your classes, packages, and
      // configuration files, via  Arquillian.
      return AbstractShellTest.getDeployment().addPackages(true, ErraiPlugin.class.getPackage());
   }
 
   @Test
   public void testInstall() throws Exception
   {
	  // Create a new barebones Java project
  	  project = initializeJavaProject();
  	  
      // Queue input lines to be read as the Shell executes.
      queueInputLines("y");
 
      // Execute a command. If any input is required, it will be read from queued input.
      getShell().execute("echo hi there");
 
      Assert.assertNotNull(resolver);
   }
   
	// TODO add test here
//	public void testReplacePackageName(){
//		
//   }
	
//   	@Test
//	public void testGetSourceFileForClass(){
//   		try {
//   	   		System.out.println(">>>>>>>>>>>>>>> !!!!!!!!!!!!!!!!!");
//   			String sourcePath = "com.example.ServiceImpl";
//   			File file = Utils.getSourceFileForClass(project, sourcePath);
//   			System.out.println("file:" + file);
//   			Assert.assertNotNull(file);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
   
}