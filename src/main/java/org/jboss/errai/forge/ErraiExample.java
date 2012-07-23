package org.jboss.errai.forge;

import org.jboss.errai.forge.facet.ErraiBaseFacet;
import org.jboss.forge.shell.plugins.PipeOut;

public abstract class ErraiExample {
	
    final ErraiPlugin plugin;

	public ErraiExample(ErraiPlugin plugin, final PipeOut pipeOut) {
	   this.plugin = plugin;
	   assertInstalled();
       createWebappFiles(pipeOut);
       createAppFiles(pipeOut);
       createResourceFiles(pipeOut);
       createTestFiles(pipeOut);
		   
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
    
    
    private void assertInstalled() {
        if (!plugin.getProject().hasFacet(ErraiBaseFacet.class)) {
            throw new RuntimeException("ErraiFacet is not installed. Use 'errai setup' to get started.");
        }
    }
    
}
