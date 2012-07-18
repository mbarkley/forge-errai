package org.jboss.errai.forge;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.InputStream;

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

    private void assertInstalled() {
        if (!project.hasFacet(ErraiFacet.class)) {
            throw new RuntimeException("ErraiFacet is not installed. Use 'errai setup' to get started.");
        }
    }

    @Command("help")
    public void exampleDefaultCommand(@Option final String opt, final PipeOut pipeOut) {
        pipeOut.println(ShellColor.BLUE, "Use the install commands to install:");
        pipeOut.println(ShellColor.BLUE, "install-errai-bus-example: a sample Errai enabled facelet file");
    }

    @Command("install-errai-bus-example")
    public void installExampleFacelets(final PipeOut pipeOut) {
        assertInstalled();
        createErraiBusWebappFiles(pipeOut);
        createErraiBusAppFiles(pipeOut);
        createErraiBusResourceFiles(pipeOut);
        createErraiBusTestFiles(pipeOut);
        //createErraiBusPomFile(pipeOut);
        
    }

    /**
     * Create a simple template file, and a Errai enabled index file that uses the template
     *
     * @param pipeOut
     */
    private void createErraiBusWebappFiles(final PipeOut pipeOut) {
        DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
        // create WEB-INF/web.xml
        DirectoryResource wiDirectory = webRoot.getOrCreateChildDirectory("WEB-INF");
        FileResource<?> wiPage = (FileResource<?>) wiDirectory.getChild("web.xml");
        InputStream stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/webapp/WEB-INF/web.xml");
        wiPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "WEB-INF/web.xml", "file"));

        // create App.css
        FileResource<?> appPage = (FileResource<?>) webRoot.getChild("App.css");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/webapp/App.css");
        appPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "App.css", "file"));

        // create App.html
        FileResource<?> apphPage = (FileResource<?>) webRoot.getChild("App.html");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/webapp/App.html");
        apphPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "App.html", "file"));
    }

    /**
     * Create an errai-bus example client & server files 
     *
     * @param pipeOut
     */
    private void createErraiBusAppFiles(final PipeOut pipeOut) {
        JavaSourceFacet source = project.getFacet(JavaSourceFacet.class);
        DirectoryResource sourceRoot = source.getBasePackageResource();
        
        DirectoryResource clDirectory = sourceRoot.getOrCreateChildDirectory("client");
        clDirectory = clDirectory.getOrCreateChildDirectory("local");
        DirectoryResource srvDirectory = sourceRoot.getOrCreateChildDirectory("server");
        
        //create client class
        FileResource<?> clientIndexPage = (FileResource<?>) clDirectory.getChild("HelloWorldClient.java");
        InputStream cStream = ErraiPlugin.class.getResourceAsStream("/errai-bus/java/client/local/HelloWorldClient.java.txt");
        clientIndexPage.setContents(replacePackageName(cStream));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "HelloWorldClient", "class"));
        
        //create server class
        FileResource<?> serverIndexPage = (FileResource<?>) srvDirectory.getChild("HelloWorldService.java");
        InputStream sStream = ErraiPlugin.class.getResourceAsStream("/errai-bus/java/server/HelloWorldService.java.txt");
        serverIndexPage.setContents(replacePackageName(sStream));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "HelloWorldService", "class"));

        //create App.gwt config file
        FileResource<?> confIndexPage = (FileResource<?>) sourceRoot.getChild("App.gwt.xml");
        InputStream cfStream = ErraiPlugin.class.getResourceAsStream("/errai-bus/java/App.gwt.xml.txt");
        confIndexPage.setContents(cfStream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "App.gwt.xml", "file"));
        
    }
    
    /**
     * Create an errai-bus example client & server files 
     *
     * @param pipeOut
     */
    private void createErraiBusResourceFiles(final PipeOut pipeOut) {
        DirectoryResource sourceRoot = project.getFacet(ResourceFacet.class).getResourceFolder();
        
        //create App props
        FileResource<?> appIndexPage = (FileResource<?>) sourceRoot.getChild("ErraiApp.properties");
        InputStream stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/properties/ErraiApp.properties");
        appIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "ErraiApp.properties", "file"));
        
        //create Service props
        FileResource<?> serviceIndexPage = (FileResource<?>) sourceRoot.getChild("ErraiService.properties");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/properties/ErraiService.properties");
        serviceIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "ErraiService.properties", "file"));
        
        //create log4j props
        FileResource<?> log4jIndexPage = (FileResource<?>) sourceRoot.getChild("log4j.properties");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/properties/log4j.properties");
        log4jIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "log4j.properties", "file"));
        
    }    
    
    /**
     * Create an errai-bus example client & server files 
     *
     * @param pipeOut
     */
    private void createErraiBusTestFiles(final PipeOut pipeOut) {
        DirectoryResource resourceRoot = project.getFacet(ResourceFacet.class).getTestResourceFolder();
        
        //create App props
        FileResource<?> appIndexPage = (FileResource<?>) resourceRoot.getChild("ErraiApp.properties");
        InputStream stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/properties/ErraiApp.properties");
        appIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "ErraiApp.properties", "file"));
        
        DirectoryResource javaRoot = project.getFacet(JavaSourceFacet.class).getTestSourceFolder();
        
        DirectoryResource clDirectory = javaRoot.getOrCreateChildDirectory("client");
        clDirectory = clDirectory.getOrCreateChildDirectory("local");
        
        //create client class
        FileResource<?> cl1IndexPage = (FileResource<?>) clDirectory.getChild("ErraiIocTestHelper.java");
        InputStream c1Stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/test/ErraiIocTestHelper.java.txt");
        cl1IndexPage.setContents(replacePackageName(c1Stream));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "ErraiIocTestHelper.java", "class"));
        
        //create client class
        FileResource<?> cl2IndexPage = (FileResource<?>) clDirectory.getChild("HelloWorldClientTest.java");
        InputStream c2Stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/test/HelloWorldClientTest.java.txt");
        cl2IndexPage.setContents(replacePackageName(c2Stream));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "HelloWorldClientTest.java", "class"));
    }
    
    /**
     * Create an errai-bus example client & server files 
     *
     * @param pipeOut
     */
    private void createErraiBusPomFile(final PipeOut pipeOut) {
        DirectoryResource resourceRoot = project.getProjectRoot();
        
        //create App props
        FileResource<?> appIndexPage = (FileResource<?>) resourceRoot.getChild("pom.xml");
        InputStream stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/pom.xml.txt");
        appIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiFacet.SUCCESS_MSG_FMT, "pom.xml", "file"));
        
    }    
    
    private String replacePackageName(InputStream templateJavaFile) {
    	try {
    		final JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
    		final String defaultJavaPackage = java.getBasePackage();
    		
            String templateJavaFileString =  new java.util.Scanner(templateJavaFile).useDelimiter("\\A").next();
            return templateJavaFileString.replace("${package}", defaultJavaPackage);
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }
}