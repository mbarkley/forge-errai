package org.jboss.errai.forge.example;

import org.jboss.errai.forge.ErraiPlugin;
import org.jboss.errai.forge.enums.ErraiFacetsEnum;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;

public class ErraiExamplePlugin {
	
	final ErraiPlugin plugin;
	
	public ErraiExamplePlugin(ErraiPlugin plugin) {
		this.plugin = plugin;
	}
	
    @Command("help-examples")
    public void exampleDefaultCommand(@Option final String opt, final PipeOut pipeOut) {
        pipeOut.println(ShellColor.BLUE, "Use the install commands to install:");
        pipeOut.println(ShellColor.BLUE, "install-errai-bus: an example of simple Errai application");
        pipeOut.println(ShellColor.BLUE, "install-errai-cdi: an example of Errai CDI-based application");
        pipeOut.println(ShellColor.BLUE, "install-errai-jaxrs: an example of Errai Jaxrs applicatiton");
        pipeOut.println(ShellColor.BLUE, "install-errai-ui: an example of Errai UI applicatiton");
        pipeOut.println(ShellColor.BLUE, "uninstall-errai-bus: uninstall simple Errai Bus application");
        pipeOut.println(ShellColor.BLUE, "uninstall-errai-cdi: uninstal Errai CDI-based application");
        pipeOut.println(ShellColor.BLUE, "uninstall-errai-jaxrs: unistall Errai Jaxrs application");
        pipeOut.println(ShellColor.BLUE, "uninstall-errai-ui: unistall Errai UI application");
    }
    
    //install examples

    @Command("install-errai-bus")
    public void installErraiBus(final PipeOut pipeOut) {
            if (plugin.getProject().hasFacet(ErraiFacetsEnum.ERRAI_EXAMPLES.getFacet())) {
            	new ErraiBusExample(this.plugin, pipeOut).install();
            } else {
                pipeOut.println("Errai Example Facet is not installed. Use 'errai setup-example' to get started.");
            }
    }
    
    @Command("install-errai-cdi")
    public void installErraiCdi(final PipeOut pipeOut) {
        if (plugin.getProject().hasFacet(ErraiFacetsEnum.ERRAI_EXAMPLES.getFacet())) {
        		new ErraiCdiExample(this.plugin, pipeOut).install();
        } else {
            pipeOut.println("Errai Example Facet is not installed. Use 'errai setup-example' to get started.");
        }
    }
    
    @Command("install-errai-jaxrs")
    public void installErraiJaxrs(final PipeOut pipeOut) {
        if (plugin.getProject().hasFacet(ErraiFacetsEnum.ERRAI_EXAMPLES.getFacet())) {
    		new ErraiJaxrsExample(this.plugin, pipeOut).install();
        } else {
            pipeOut.println("Errai Example Facet is not installed. Use 'errai setup-example' to get started.");
        }
    }
    
    @Command("install-errai-ui")
    public void installErraiUI(final PipeOut pipeOut) {
        if (plugin.getProject().hasFacet(ErraiFacetsEnum.ERRAI_EXAMPLES.getFacet())) {
    		new ErraiUIExample(this.plugin, pipeOut).install();
        } else {
            pipeOut.println("Errai Example Facet is not installed. Use 'errai setup-example' to get started.");
        }
    }
    
    
    //uninstall modules
    
//    @Command("uninstall-errai-bus")
//    public void uninstallErraiBus(final PipeOut pipeOut) {
//        if (project.hasFacet(ErraiExampleFacetEnum.ERRAI_BUS_FACET.getFacet())) {
//        	if(this.isModuleInstalled()){
//        		new ErraiBusExample(this, pipeOut).uninstall();
//        	}
//        	else {
//        		pipeOut.println("Errai Bus is not installed, can not uninstall.");
//        	}
//        } else {
//            pipeOut.println("Errai Bus Facet is not installed. Use 'errai setup' to get started.");
//        }
//    }
//    
//    @Command("uninstall-errai-cdi")
//    public void uninstallErraiCdi(final PipeOut pipeOut) {
//        if (project.hasFacet(ErraiExampleFacetEnum.ERRAI_CDI_FACET.getFacet())) {
//        	if(this.isModuleInstalled()){
//        		new ErraiCdiExample(this, pipeOut).uninstall();
//        	}
//        	else {
//        		pipeOut.println("Errai CDI is not installed, can not uninstall.");
//        	}
//        } else {
//            pipeOut.println("Errai CDI Facet is not installed. Use 'errai setup' to get started.");
//        }
//    }
//    
//    @Command("uninstall-errai-jaxrs")
//    public void uninstallErraiJaxrs(final PipeOut pipeOut) {
//        if (project.hasFacet(ErraiExampleFacetEnum.ERRAI_JAXRS_FACET.getFacet())) {
//        	if(this.isModuleInstalled()){
//        		new ErraiJaxrsExample(this, pipeOut).uninstall();
//        	}
//        	else {
//        		pipeOut.println("Errai Jaxrs is is not installed, can not uninstall.");
//        	}
//        } else {
//            pipeOut.println("Errai Jaxrs Facet is not installed. Use 'errai setup' to get started.");
//        }
//    }
//    
//    @Command("uninstall-errai-ui")
//    public void uninstallErraiUI(final PipeOut pipeOut) {
//        if (project.hasFacet(ErraiExampleFacetEnum.ERRAI_UI_FACET.getFacet())) {
//        	if(this.isModuleInstalled()){
//        		new ErraiUIExample(this, pipeOut).uninstall();
//        	}
//        	else {
//        		pipeOut.println("Errai UI is is not installed, can not uninstall.");
//        	}
//        } else {
//            pipeOut.println("Errai UI Facet is not installed. Use 'errai setup' to get started.");
//        }
//    }
    

}
