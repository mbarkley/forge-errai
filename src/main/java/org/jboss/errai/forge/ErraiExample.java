package org.jboss.errai.forge;

import org.jboss.errai.forge.facet.ErraiBaseFacet;
import org.jboss.forge.shell.plugins.PipeOut;

public abstract class ErraiExample {
	
    final ErraiPlugin plugin;
    final PipeOut pipeOut;
    
	public ErraiExample(ErraiPlugin plugin, final PipeOut pipeOut) {
	   this.plugin = plugin;
	   this.pipeOut = pipeOut;
    }
	
	public void install (){
	   assertFacetInstalled();
	   createWebappFiles(pipeOut);
       createAppFiles(pipeOut);
       createResourceFiles(pipeOut);
       createTestFiles(pipeOut);
       plugin.setModuleInstalled(true);
	   pipeOut.println("Sucesfully  installed... :)");
	}
	
	public void uninstall (){
	   deleteWebappFiles(pipeOut);
       deleteAppFiles(pipeOut);
       deleteResourceFiles(pipeOut);
       deleteTestFiles(pipeOut);
       plugin.setModuleInstalled(false);
	   pipeOut.println("Sucesfully  uninstalled, what a pitty... :(");
       
		
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
     * Create an errai-bus example client & server files 
     *
     * @param pipeOut
     */
    abstract void createTestFiles(final PipeOut pipeOut);
    
    
    // uninstall 
    
    /**
     * Create a webapp files
     *
     * @param pipeOut
     */
    abstract void deleteWebappFiles(final PipeOut pipeOut);
    
    /**
     * Create an application files 
     *
     * @param pipeOut
     */
    abstract void deleteAppFiles(final PipeOut pipeOut);
    
    /**
     * Create a resource files 
     *
     * @param pipeOut
     */
    abstract void deleteResourceFiles(final PipeOut pipeOut);    
    
    /**
     * Create an errai-bus example client & server files 
     *
     * @param pipeOut
     */
    abstract void deleteTestFiles(final PipeOut pipeOut);
    
    
    
    private void assertFacetInstalled() {
        if (!plugin.getProject().hasFacet(ErraiBaseFacet.class)) {
            throw new RuntimeException("ErraiFacet is not installed. Use 'errai setup' to get started.");
        }
    }
    
}
