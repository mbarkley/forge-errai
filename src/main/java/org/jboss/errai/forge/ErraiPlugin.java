package org.jboss.errai.forge;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.forge.example.ErraiBusExample;
import org.jboss.errai.forge.example.ErraiCdiExample;
import org.jboss.errai.forge.example.ErraiExamplePlugin;
import org.jboss.errai.forge.example.ErraiJaxrsExample;
import org.jboss.errai.forge.example.ErraiUIExample;
import org.jboss.errai.forge.facet.ErraiBaseFacet;
import org.jboss.errai.forge.facet.ErraiBusCommandsEnum;
import org.jboss.errai.forge.facet.ErraiBusFacet;
import org.jboss.errai.forge.facet.ErraiExampleFacet;
import org.jboss.errai.forge.facet.ErraiExamplesCommandsEnum;
import org.jboss.errai.forge.facet.ErraiFacetsEnum;
import org.jboss.errai.forge.facet.ErraiGeneratorCommandsEnum;
import org.jboss.errai.forge.facet.ErraiInstalled;
import org.jboss.errai.forge.gen.Generator;
import org.jboss.errai.forge.template.Velocity;
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
import org.jboss.forge.shell.plugins.SetupCommand;

/**
 * @author pslegr
 */
@Alias("errai")
@RequiresProject
public class ErraiPlugin implements Plugin {

    final Project project;
    final Event<InstallFacets> installFacets;
    
    final ErraiExamplePlugin examplePlugin;
    
    @Inject
    private ShellPrompt prompt;
    
    private Velocity velocity;
    
    private Generator generator;
    
	public void setModuleInstalled(boolean isModuleInstalled) {
		ErraiInstalled.getInstance().setInstalled(isModuleInstalled);
	}
	

	@Inject
    public ErraiPlugin(final Project project, final Event<InstallFacets> event) {
        this.project = project;
        this.installFacets = event;
        this.examplePlugin = new ErraiExamplePlugin(this);
        this.velocity = new Velocity(this.getProject());
        this.generator = new Generator(project, velocity);
        
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

	@SetupCommand
	@Command("setup")
    public void setup(@Option final ErraiFacetsEnum module, final PipeOut out) {
		if (!project.hasFacet(module.getFacet())) {
		     installFacets.fire(new InstallFacets(module.getFacet()));
		}
		if (project.hasFacet(module.getFacet())) {
			 ShellMessages.success(out, module + " is configured.");
		}
    }
	
    //examples
	@Command("examples")
    public void errai_examples_setup(@Option final ErraiExamplesCommandsEnum command, final PipeOut out) {
		
		//setup examples facet
		switch (command) {
		case ERRAI_EXAMPLES_SETUP:
			installFacets.fire(new InstallFacets(ErraiExampleFacet.class));
			return;
		}
		
		//check example facet installation
        if (!getProject().hasFacet(ErraiFacetsEnum.ERRAI_EXAMPLES.getFacet())) {
        	out.println("Errai Example Facet is not installed. Use 'errai examples setup' to get started.");
        	return;
        }
		
        //example commands options
		switch (command) {
			case ERRAI_EXAMPLES_HELP:
				//help
		        out.println(ShellColor.BLUE, "Use the install commands to install:");
		        out.println(ShellColor.BLUE, "'examples install-errai-bus': an example of simple Errai application");
		        out.println(ShellColor.BLUE, "'examples install-errai-cdi': an example of Errai CDI-based application");
		        out.println(ShellColor.BLUE, "'examples install-errai-jaxrs': an example of Errai Jaxrs applicatiton");
		        out.println(ShellColor.BLUE, "'examples install-errai-ui': an example of Errai UI applicatiton");
		        out.println(ShellColor.BLUE, "'examples uninstall-errai-bus': uninstall simple Errai Bus application");
		        out.println(ShellColor.BLUE, "'examples uninstall-errai-cdi': uninstal Errai CDI-based application");
		        out.println(ShellColor.BLUE, "'examples uninstall-errai-jaxrs': unistall Errai Jaxrs application");
		        out.println(ShellColor.BLUE, "'examples uninstall-errai-ui': unistall Errai UI application");
				break;
				
			case ERRAI_EXAMPLES_INSTALL_ERRAI_BUS:	
            	new ErraiBusExample(this, out).install();break;
			case ERRAI_EXAMPLES_INSTALL_ERRAI_CDI:
        		new ErraiCdiExample(this, out).install();break;
			case ERRAI_EXAMPLES_INSTALL_ERRAI_JAXRS:
	    		new ErraiJaxrsExample(this, out).install();break;
			case ERRAI_EXAMPLES_INSTALL_ERRAI_UI:
	    		new ErraiUIExample(this, out).install();break;
			case ERRAI_EXAMPLES_UNINSTALL_ERRAI_BUS:	
            	new ErraiBusExample(this, out).uninstall();break;
			case ERRAI_EXAMPLES_UNINSTALL_ERRAI_CDI:
        		new ErraiCdiExample(this, out).uninstall();break;
			case ERRAI_EXAMPLES_UNINSTALL_ERRAI_JAXRS:
	    		new ErraiJaxrsExample(this, out).uninstall();break;
			case ERRAI_EXAMPLES_UNINSTALL_ERRAI_UI:
	    		new ErraiUIExample(this, out).uninstall();break;
		}
    }
	
	//errai-bus
	@Command("bus")
    public void errai_bus_setup(@Option final ErraiBusCommandsEnum command,@Option(name="from") String from, final PipeOut out) {
		
		//setup bus facet
		switch (command) {
		case ERRAI_BUS_SETUP:
			installFacets.fire(new InstallFacets(ErraiBusFacet.class));
			return;
		}
		
		//check bus facet installation
        if (!getProject().hasFacet(ErraiFacetsEnum.ERRAI_BUS.getFacet())) {
        	out.println("Errai Bus Facet is not installed. Use 'errai bus setup' to get started.");
        	return;
        }
		
        //bus commands options
		if(command.equals(ErraiBusCommandsEnum.ERRAI_BUS_GENERATE_EMPTY_SERVICE_CLASS)){
			// TODO @Service Impl empty template generation
			velocity.createJavaSourceWithTemplateName("YourEmptyServiceImpl.java.vm");
		}
		if(command.equals(ErraiBusCommandsEnum.ERRAI_BUS_GENERATE_SIMPLE_SERVICE_CLASS)){
			// TODO @Service Impl empty template generation
			velocity.createJavaSourceWithTemplateName("SimpleServiceImpl.java.vm");
		}
		if(command.equals(ErraiBusCommandsEnum.ERRAI_BUS_GENERATE_REMOTES_FROM_ALL_SERVICE_CLASSES)){
			// generate @Remote interfaces for all @Service classes
			generator.generate(ErraiGeneratorCommandsEnum.ERRAI_BUS_GENERATE_REMOTES_FROM_ALL_SERVICE_CLASSES);
		}
		if(command.equals(ErraiBusCommandsEnum.ERRAI_BUS_GENERATE_REMOTE_FROM_SERVICE_CLASS)){
			// generate @Remote interface for given @Service class
	        System.out.println("from: " + from);
			if(from != null){
				generator.generate(ErraiGeneratorCommandsEnum.ERRAI_BUS_GENERATE_REMOTE_FROM_SERVICE_CLASS,from);
			}
		}
		if(command.equals(ErraiBusCommandsEnum.ERRAI_BUS_RPC_INVOKE_ENDPOINT)){
			// do rpc invoke
	        out.println(ShellColor.BLUE, "do rpc invoke:");
		}
    }

	
}