package org.jboss.errai.forge;

import java.util.Arrays;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.forge.facet.ErraiBaseFacet;
import org.jboss.errai.forge.facet.ErraiFacets;
import org.jboss.errai.forge.facet.ErraiInstalled;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;

/**
 * @author pslegr
 */
@Alias("errai")
@RequiresProject
public class ErraiPlugin implements Plugin {

    private final Project project;
    private final Event<InstallFacets> installFacets;
    
    @Inject
    private ShellPrompt prompt;
    

    public boolean isModuleInstalled() {
		return ErraiInstalled.getInstance().isInstalled();
	}

	public void setModuleInstalled(boolean isModuleInstalled) {
		ErraiInstalled.getInstance().setInstalled(isModuleInstalled);
	}

	@Inject
    public ErraiPlugin(final Project project, final Event<InstallFacets> event) {
        this.project = project;
        this.installFacets = event;
    }

    public Project getProject() {
		return project;
	}

	public Event<InstallFacets> getInstallFacets() {
		return installFacets;
	}

	@DefaultCommand
    public void status(final PipeOut out) {
        if (project.hasFacet(ErraiBaseFacet.class)) {
            out.println("Errai is installed.");
        } else {
            out.println("Errai is not installed. Use 'errai setup' to get started.");
        }
    }

    // confirmed working
    @Command("setup")
    public void setup(final PipeOut out) {
//        if (!project.hasFacet(ErraiFacet.class)) {
//            installFacets.fire(new InstallFacets(ErraiFacet.class));
//        }
//        if (project.hasFacet(ErraiFacet.class)) {
//            ShellMessages.success(out, "ErraiFacet is configured.");
//        }
    	
		ErraiFacets module = prompt.promptChoiceTyped("Which Errai module to install?",
        Arrays.asList(ErraiFacets.values()), ErraiFacets.ERRAI_BUS_FACET);
		
		if (!project.hasFacet(module.getFacet())) {
		     installFacets.fire(new InstallFacets(module.getFacet()));
		}
		if (project.hasFacet(module.getFacet())) {
			 ShellMessages.success(out, module + " is configured.");
		}
		
		//TODO implement here logic for istalling only one facet at the time, once one facet is isntalled the others
		// won't be used 
		
    	
    }

    @Command("help")
    public void exampleDefaultCommand(@Option final String opt, final PipeOut pipeOut) {
        pipeOut.println(ShellColor.BLUE, "Use the install commands to install:");
        pipeOut.println(ShellColor.BLUE, "install-errai-bus: an example of simple Errai application");
        pipeOut.println(ShellColor.BLUE, "install-errai-cdi: an example of Errai CDI-based application");
        pipeOut.println(ShellColor.BLUE, "install-errai-jaxrs: an example of Errai Jaxrs applicatiton");
        pipeOut.println(ShellColor.BLUE, "uninstall-errai-bus: uninstall simple Errai Bus application");
        pipeOut.println(ShellColor.BLUE, "uninstall-errai-cdi: uninstal Errai CDI-based application");
        pipeOut.println(ShellColor.BLUE, "uninstall-errai-jaxrs: unistall Errai Jaxrs application");
    }
    
    //install modules

    @Command("install-errai-bus")
    public void installErraiBus(final PipeOut pipeOut) {
        if (project.hasFacet(ErraiFacets.ERRAI_BUS_FACET.getFacet())) {
        	if(!this.isModuleInstalled()){
        		new ErraiBusExample(this, pipeOut).install();
        	}
        	else {
        		pipeOut.println("Errai Bus is installed.");
        	}
        } else {
            pipeOut.println("Errai Bus Facet is not installed. Use 'errai setup' to get started.");
        }
    }
    
    @Command("install-errai-cdi")
    public void installErraiCdi(final PipeOut pipeOut) {
        if (project.hasFacet(ErraiFacets.ERRAI_CDI_FACET.getFacet())) {
        	if(!this.isModuleInstalled()){
        		new ErraiCdiExample(this, pipeOut).install();
        	}
        	else {
        		pipeOut.println("Errai CDI is installed.");
        	}
        } else {
            pipeOut.println("Errai CDI Facet is not installed. Use 'errai setup' to get started.");
        }
    }
    
    @Command("install-errai-jaxrs")
    public void installErraiJaxrs(final PipeOut pipeOut) {
        if (project.hasFacet(ErraiFacets.ERRAI_JAXRS_FACET.getFacet())) {
        	if(!this.isModuleInstalled()){
        		new ErraiJaxrsExample(this, pipeOut).install();
        	}
        	else {
        		pipeOut.println("Errai Jaxrs is installed.");
        	}
        } else {
            pipeOut.println("Errai Jaxrs Facet is not installed. Use 'errai setup' to get started.");
        }
    }
    
    //uninstall modules
    
    @Command("uninstall-errai-bus")
    public void uninstallErraiBus(final PipeOut pipeOut) {
        if (project.hasFacet(ErraiFacets.ERRAI_BUS_FACET.getFacet())) {
        	if(this.isModuleInstalled()){
        		new ErraiBusExample(this, pipeOut).uninstall();
        	}
        	else {
        		pipeOut.println("Errai Bus is not installed, can not uninstall.");
        	}
        } else {
            pipeOut.println("Errai Bus Facet is not installed. Use 'errai setup' to get started.");
        }
    }
    
    @Command("uninstall-errai-cdi")
    public void uninstallErraiCdi(final PipeOut pipeOut) {
        if (project.hasFacet(ErraiFacets.ERRAI_CDI_FACET.getFacet())) {
        	if(this.isModuleInstalled()){
        		new ErraiCdiExample(this, pipeOut).uninstall();
        	}
        	else {
        		pipeOut.println("Errai CDI is not installed, can not uninstall.");
        	}
        } else {
            pipeOut.println("Errai CDI Facet is not installed. Use 'errai setup' to get started.");
        }
    }
    
    @Command("uninstall-errai-jaxrs")
    public void uninstallErraiJaxrs(final PipeOut pipeOut) {
        if (project.hasFacet(ErraiFacets.ERRAI_JAXRS_FACET.getFacet())) {
        	if(this.isModuleInstalled()){
        		new ErraiJaxrsExample(this, pipeOut).uninstall();
        	}
        	else {
        		pipeOut.println("Errai Jaxrs is is not installed, can not uninstall.");
        	}
        } else {
            pipeOut.println("Errai Jaxrs Facet is not installed. Use 'errai setup' to get started.");
        }
    }
}