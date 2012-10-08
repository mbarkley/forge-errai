package org.jboss.errai.forge.facet;

import org.jboss.errai.forge.example.ErraiExampleEnum;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.resources.DirectoryResource;

/**
 * @author pslegr
 */
public class ErraiExampleFacet extends BaseFacet
{
    private DirectoryResource exampleRoot;
    ErraiExampleEnum selectedExample;
    
   @Override
   public boolean isInstalled(){
       if (!project.hasFacet(ErraiExampleFacet.class)) {
   		return false;
       }
		return true;
   }	
   
		
	public boolean install (){
  	   prepareStructure();
	   return true;
	}
	
	private void prepareStructure(){
		DirectoryResource projectRoot = project.getProjectRoot();
        DirectoryResource srcDirectory = projectRoot.getOrCreateChildDirectory("src");
        exampleRoot = srcDirectory.getOrCreateChildDirectory("examples");
        
	}
	
	public DirectoryResource getExampleRootDirectory(){
		return exampleRoot.getOrCreateChildDirectory(getSelectedExample().toString());
	}
	
	
	public DirectoryResource getWebRootDirectory(){
		return getExampleRootDirectory().getOrCreateChildDirectory("src").getOrCreateChildDirectory("main").getOrCreateChildDirectory("webapp");
	}
	
	public DirectoryResource getBasePackageDirectory(){
		return getExampleRootDirectory().getOrCreateChildDirectory("src").getOrCreateChildDirectory("main").getOrCreateChildDirectory("java");
	}
	
	public DirectoryResource getResourceDirectory(){
		return getExampleRootDirectory().getOrCreateChildDirectory("src").getOrCreateChildDirectory("main").getOrCreateChildDirectory("resources");
	}
	
	public DirectoryResource getTestResourceDirectory(){
		return getExampleRootDirectory().getOrCreateChildDirectory("src").getOrCreateChildDirectory("test").getOrCreateChildDirectory("resources");
	}

	public DirectoryResource getTestBasePackageDirectory(){
		return getExampleRootDirectory().getOrCreateChildDirectory("src").getOrCreateChildDirectory("test").getOrCreateChildDirectory("resources");
	}


	public ErraiExampleEnum getSelectedExample() {
		return selectedExample;
	}


	public void setSelectedExample(ErraiExampleEnum selectedExample) {
		this.selectedExample = selectedExample;
	}
}
