package org.jboss.errai.forge;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
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
        if (project.hasFacet(ErraiFacet.class)) {
            out.println("Errai is installed.");
        } else {
            out.println("Errai is not installed. Use 'errai setup' to get started.");
        }
    }

    // confirmed working
    @Command("setup")
    public void setup(final PipeOut out) {
        if (!project.hasFacet(ErraiFacet.class)) {
            installFacets.fire(new InstallFacets(ErraiFacet.class));
        }
        if (project.hasFacet(ErraiFacet.class)) {
            ShellMessages.success(out, "ErraiFacet is configured.");
        }
    }

    @Command("help")
    public void exampleDefaultCommand(@Option final String opt, final PipeOut pipeOut) {
        pipeOut.println(ShellColor.BLUE, "Use the install commands to install:");
        pipeOut.println(ShellColor.BLUE, "install-errai-bus-example: an example of simple Errai application");
        pipeOut.println(ShellColor.BLUE, "install-errai-cdi-example: an example of Errai CDI-based applicaiton");
    }

    @Command("install-errai-bus-example")
    public void installErraiBusExample(final PipeOut pipeOut) {
        new ErraiBusExample(this, pipeOut); 
    }
    
    @Command("install-errai-cdi-example")
    public void installErraiCdiExample(final PipeOut pipeOut) {
        new ErraiCdiExample(this, pipeOut); 
    }
    
}