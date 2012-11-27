package org.jboss.errai.forge.example;

import java.io.InputStream;

import org.jboss.errai.forge.ErraiPlugin;
import org.jboss.errai.forge.Utils;
import org.jboss.errai.forge.facet.ErraiBaseFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.PipeOut;

public class ErraiUIExample extends AbstractExample{
	
	public ErraiUIExample(final ErraiPlugin plugin, final PipeOut pipeOut) {
		super(plugin,pipeOut,ErraiExampleEnum.ERRAI_UI_EXAMPLE);   }
	
    void createWebappFiles(final PipeOut pipeOut) {
        DirectoryResource webRoot = erraiExampleFacet.getWebRootDirectory();
        // create WEB-INF/web.xml
        DirectoryResource wiDirectory = webRoot.getOrCreateChildDirectory("WEB-INF");
        FileResource<?> wiPage = (FileResource<?>) wiDirectory.getChild("web.xml");
        InputStream stream = ErraiPlugin.class.getResourceAsStream("/errai-ui/webapp/WEB-INF/web.xml");
        wiPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "WEB-INF/web.xml", "file"));
        
        // create App.css
        FileResource<?> appPage = (FileResource<?>) webRoot.getChild("App.css");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-ui/webapp/App.css");
        appPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "App.css", "file"));

        // create App.html
        FileResource<?> apphPage = (FileResource<?>) webRoot.getChild("App.html");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-ui/webapp/App.html");
        apphPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "App.html", "file"));
    }

    void createAppFiles(final PipeOut pipeOut) {
        DirectoryResource sourceRoot = erraiExampleFacet.getBasePackageDirectory();
        
        DirectoryResource clientDirectory = sourceRoot.getOrCreateChildDirectory("client");
        DirectoryResource localDirectory = clientDirectory.getOrCreateChildDirectory("local");
        DirectoryResource sharedDirectory = clientDirectory.getOrCreateChildDirectory("shared");
        DirectoryResource srvDirectory = sourceRoot.getOrCreateChildDirectory("server");
        
        //create client classes
        FileResource<?> localIndexPage = (FileResource<?>) localDirectory.getChild("App.java");
        InputStream cStream = ErraiPlugin.class.getResourceAsStream("/errai-ui/java/client/local/App.java.txt");
        localIndexPage.setContents(Utils.replacePackageName(cStream,plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "App", "class"));
        
        FileResource<?> localIndexPage2 = (FileResource<?>) localDirectory.getChild("App.html");
        cStream = ErraiPlugin.class.getResourceAsStream("/errai-ui/java/client/local/App.html.txt");
        localIndexPage2.setContents(Utils.replacePackageName(cStream,plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "App", "file"));
        
        FileResource<?> localIndexPage3 = (FileResource<?>) localDirectory.getChild("Spotlight.java");
        cStream = ErraiPlugin.class.getResourceAsStream("/errai-ui/java/client/local/Spotlight.java.txt");
        localIndexPage3.setContents(Utils.replacePackageName(cStream,plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "Spotlight", "class"));

        FileResource<?> shared1IndexPage = (FileResource<?>) sharedDirectory.getChild("Message.java");
        cStream = ErraiPlugin.class.getResourceAsStream("/errai-ui/java/client/shared/Message.java.txt");
        shared1IndexPage.setContents(Utils.replacePackageName(cStream,plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "Message", "class"));
        
        FileResource<?> shared2IndexPage = (FileResource<?>) sharedDirectory.getChild("Response.java");
        cStream = ErraiPlugin.class.getResourceAsStream("/errai-ui/java/client/shared/Response.java.txt");
        shared2IndexPage.setContents(Utils.replacePackageName(cStream,plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "Response", "class"));
        
        FileResource<?> shared3IndexPage = (FileResource<?>) sharedDirectory.getChild("Profile.java");
        cStream = ErraiPlugin.class.getResourceAsStream("/errai-ui/java/client/shared/Profile.java.txt");
        shared3IndexPage.setContents(Utils.replacePackageName(cStream,plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "Profile", "class"));
        
        
        //create server class
        FileResource<?> serverIndexPage = (FileResource<?>) srvDirectory.getChild("MessageListener.java");
        InputStream sStream = ErraiPlugin.class.getResourceAsStream("/errai-ui/java/server/MessageListener.java.txt");
        serverIndexPage.setContents(Utils.replacePackageName(sStream,plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "MessageListener", "class"));

        //create App.gwt config file
        FileResource<?> confIndexPage = (FileResource<?>) sourceRoot.getChild("App.gwt.xml");
        InputStream cfStream = ErraiPlugin.class.getResourceAsStream("/errai-ui/java/App.gwt.xml.txt");
        confIndexPage.setContents(cfStream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "App.gwt.xml", "file"));
        
    }
    
    void createResourceFiles(final PipeOut pipeOut) {
        DirectoryResource sourceRoot = erraiExampleFacet.getResourceDirectory();
        
        //create App props
        FileResource<?> appIndexPage = (FileResource<?>) sourceRoot.getChild("ErraiApp.properties");
        InputStream stream = ErraiPlugin.class.getResourceAsStream("/errai-ui/resources/ErraiApp.properties");
        appIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "ErraiApp.properties", "file"));
        
        //create Service props
        FileResource<?> serviceIndexPage = (FileResource<?>) sourceRoot.getChild("ErraiService.properties");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-ui/resources/ErraiService.properties");
        serviceIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "ErraiService.properties", "file"));
        
        //create log4j props
        FileResource<?> log4jIndexPage = (FileResource<?>) sourceRoot.getChild("log4j.properties");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-ui/resources/log4j.properties");
        log4jIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "log4j.properties", "file"));
        
        //create login config
        FileResource<?> loginIndexPage = (FileResource<?>) sourceRoot.getChild("login.config");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-ui/resources/login.config");
        loginIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "login.config", "file"));
        
        //create roles properties
        FileResource<?> rolesIndexPage = (FileResource<?>) sourceRoot.getChild("roles.properties");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-ui/resources/roles.properties");
        rolesIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "roles.properties", "file"));
        
        //create users properties
        FileResource<?> usersIndexPage = (FileResource<?>) sourceRoot.getChild("users.properties");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-ui/resources/users.properties");
        usersIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "users.properties", "file"));
    }    
    
    void createTestFiles(final PipeOut pipeOut) {
    }

	@Override
	void generatePOMFile(PipeOut pipeOut) {
        DirectoryResource exampleRoot = erraiExampleFacet.getExampleRootDirectory();
        //create pom file
        FileResource<?> appIndexPage = (FileResource<?>) exampleRoot.getChild("pom.xml");
        InputStream stream = ErraiPlugin.class.getResourceAsStream("/errai-ui/pom.xml");
        appIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "pom.xml", "file"));
	}
}
