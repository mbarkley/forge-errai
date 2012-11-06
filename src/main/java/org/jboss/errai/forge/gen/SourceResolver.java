package org.jboss.errai.forge.gen;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.jboss.errai.forge.Utils;
import org.jboss.errai.forge.enums.ResourcesEnum;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;

public class SourceResolver {

	Project project;
	List<File> resources;
	String searchString;
	
	public SourceResolver(Project project) {
		this.project = project;
	}
	
	public List<File> searchForAllResources(ResourcesEnum resources){
		this.resources = new ArrayList<File>();
		this.searchString = resources.toString();
		DirectoryResource root = project.getProjectRoot();
		File[] files = root.getUnderlyingResourceObject().listFiles();
		iterateFiles(files);
		return this.resources;
		
	}
	
	public File getSourceFileForClass(String sourceClass) {
		try {
			return Utils.getSourceFileForClass(project, sourceClass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	public List<File> searchForAllResources(String sourcePackage, boolean recursive) {
		return recursive == true ? this.searchForAllResourcesRecursive(sourcePackage) : this.searchForAllResources(sourcePackage);
	}	
	
	public List<File> searchForAllResourcesRecursive(String sourcePackage){
		try {
			System.out.println(">>> search recursive");
			if(sourcePackage.endsWith(".*")) {
				sourcePackage = sourcePackage.substring(0, sourcePackage.length() -2 );
			}
			this.resources = new ArrayList<File>();
			this.searchString = resources.toString();
			DirectoryResource root = Utils.getSourcePackageAsDirResource(project, sourcePackage);
			System.out.println("root: " + root.getUnderlyingResourceObject().getAbsolutePath());
			File[] files = root.getUnderlyingResourceObject().listFiles();
			System.out.println("filesxxx: " + Arrays.asList(files).toString());
			iterateFiles(files);
			return this.resources;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<File> searchForAllResources(String sourcePackage){
		try {
			this.resources = new ArrayList<File>();
			this.searchString = resources.toString();
			DirectoryResource root = Utils.getSourcePackageAsDirResource(project, sourcePackage);
			File[] files = root.getUnderlyingResourceObject().listFiles();
			System.out.println("files: "+ files);
			
			//search for all resources in the given package
			if(sourcePackage.endsWith(".*")) {
				return Arrays.asList(files);
			}
			//search for resource for given name
			else {
				this.resources.add(Utils.getSourceFileForClass(project, sourcePackage));
				return this.resources;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<String> searchForAllResourcesFQN(String sourcePackage, boolean recursive){
		try {
			System.out.println("recursive ?: "+ recursive);
			List<String> resourcesFQNs = new ArrayList<String>();
			// get resource Files
			List<File> resources = recursive == true ? this.searchForAllResourcesRecursive(sourcePackage) : this.searchForAllResources(sourcePackage);
			System.out.println("resourcesFQN: "+ resources);
			
			// make sure all resources exist and are Java Types
			for (File file : resources) {
				JavaClass js = JavaParser.parse(JavaClass.class,file);
				if(js != null) {
					resourcesFQNs.add(js.getQualifiedName());
				}
			}
			return resourcesFQNs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
		
	private void iterateFiles(File[] files) {
	    for (File file : files) {
	        if (file.isDirectory()) {
	            iterateFiles(file.listFiles()); // Calls same method again.
	        } else {
	        	if(find(file,this.searchString)){
	        		this.resources.add(file);
	        	}
	        }
	    }
	}
	
	private boolean find(File f, String searchString) {
        boolean result = false;
        Scanner in = null;
        try {
            in = new Scanner(new FileReader(f));
            while(in.hasNextLine() && !result) {
                result = in.nextLine().indexOf(searchString) >= 0;
            }
        }
        catch(IOException e) {
            e.printStackTrace();      
        }
        finally {
            try { in.close() ; } catch(Exception e) { /* ignore */ }  
        }
        return result;
    }

	public List<File> getResources() {
		return resources;
	}
}
