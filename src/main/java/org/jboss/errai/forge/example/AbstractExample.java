package org.jboss.errai.forge.example;

import org.jboss.errai.forge.ErraiPlugin;
import org.jboss.errai.forge.facet.ErraiExampleFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ErraiExampleFacet.class})
public abstract class AbstractExample {

    final ErraiPlugin plugin;
    final PipeOut pipeOut;
    DirectoryResource exampleRoot;
    ErraiExampleFacet erraiExampleFacet;
    
    
	public AbstractExample(ErraiPlugin plugin, final PipeOut pipeOut, ErraiExampleEnum example) {
		this.plugin = plugin;
		this.pipeOut = pipeOut;
		this.erraiExampleFacet = plugin.getProject().getFacet(ErraiExampleFacet.class);
		this.erraiExampleFacet.setSelectedExample(example);
    }
	
	public void install (){
		assertFacetInstalled();
		createWebappFiles(pipeOut);
	    createAppFiles(pipeOut);
	    createResourceFiles(pipeOut);
	    createTestFiles(pipeOut);
	    generatePOMFile(pipeOut);
		pipeOut.println("Sucesfully installed... :)");
	}
	
	public boolean uninstall (){
	   deleteExample(pipeOut);
	   pipeOut.println("Sucesfully  uninstalled, what a pitty... :(");
	   return true;
	}
	
    /**
     * Create a webapp files
     *
     * @param pipeOut
     */
    abstract void createWebappFiles(final PipeOut pipeOut);
    
    /**
     * Create an application files 
     *
     * @param pipeOut
     */
    abstract void createAppFiles(final PipeOut pipeOut);
    
    /**
     * Create a resource files 
     *
     * @param pipeOut
     */
    abstract void createResourceFiles(final PipeOut pipeOut);    
    
    /**
     * Create an example client & server files 
     *
     * @param pipeOut
     */
    abstract void createTestFiles(final PipeOut pipeOut);
    
    /**
     * Create an example pom file 
     *
     * @param pipeOut
     */
    abstract void generatePOMFile(final PipeOut pipeOut);
    
    
    
    // uninstall 
    
    /**
     * Delete example application
     *
     * @param pipeOut
     */
	void deleteExample(PipeOut pipeOut) {
		// TODO Auto-generated method stub
        DirectoryResource exampleRoot = plugin.getProject().getFacet(ErraiExampleFacet.class).getExampleRootDirectory();
        exampleRoot.delete(true);
	}
    
    
    
    
    private void assertFacetInstalled() {
        if (!plugin.getProject().hasFacet(ErraiExampleFacet.class)) {
            throw new RuntimeException("ErraiExampleFacet is not installed. Use 'errai setup-example' to get started.");
        }
    }
    
}