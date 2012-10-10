package org.jboss.errai.forge.gen;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jboss.errai.forge.Utils;
import org.jboss.errai.forge.template.ResourcesEnum;
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
	
	public List<File> searchForAllResources(String sourcePackage){
		try {
			this.resources = new ArrayList<File>();
			this.searchString = resources.toString();
			DirectoryResource root = Utils.getSourcePackageAsDirResource(project, sourcePackage);
			File[] files = root.getUnderlyingResourceObject().listFiles();
			iterateFiles(files);
			return this.resources;
			
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
