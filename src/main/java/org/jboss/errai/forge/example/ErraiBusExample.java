package org.jboss.errai.forge.example;

import java.io.InputStream;

import org.jboss.errai.forge.ErraiPlugin;
import org.jboss.errai.forge.Utils;
import org.jboss.errai.forge.facet.ErraiBaseFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.PipeOut;

public class ErraiBusExample extends AbstractExample{
	
	public ErraiBusExample(final ErraiPlugin plugin, final PipeOut pipeOut) {
		super(plugin,pipeOut,ErraiExampleEnum.ERRAI_BUS_EXAMPLE);
   }
	
    void createWebappFiles(final PipeOut pipeOut) {
        DirectoryResource webRoot = erraiExampleFacet.getWebRootDirectory();
        // create WEB-INF/web.xml
        DirectoryResource wiDirectory = webRoot.getOrCreateChildDirectory("WEB-INF");
        FileResource<?> wiPage = (FileResource<?>) wiDirectory.getChild("web.xml");
        InputStream stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/webapp/WEB-INF/web.xml");
        wiPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "WEB-INF/web.xml", "file"));

        // create App.css
        FileResource<?> appPage = (FileResource<?>) webRoot.getChild("App.css");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/webapp/App.css");
        appPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "App.css", "file"));

        // create App.html
        FileResource<?> apphPage = (FileResource<?>) webRoot.getChild("App.html");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/webapp/App.html");
        apphPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "App.html", "file"));
    }

    void createAppFiles(final PipeOut pipeOut) {
        DirectoryResource sourceRoot = erraiExampleFacet.getBasePackageDirectory();
        
        DirectoryResource clDirectory = sourceRoot.getOrCreateChildDirectory("client");
        clDirectory = clDirectory.getOrCreateChildDirectory("local");
        DirectoryResource srvDirectory = sourceRoot.getOrCreateChildDirectory("server");
        
        //create client class
        FileResource<?> clientIndexPage = (FileResource<?>) clDirectory.getChild("HelloWorldClient.java");
        InputStream cStream = ErraiPlugin.class.getResourceAsStream("/errai-bus/java/client/local/HelloWorldClient.java.txt");
        clientIndexPage.setContents(Utils.replacePackageName(cStream,plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "HelloWorldClient", "class"));
        
        //create server class
        FileResource<?> serverIndexPage = (FileResource<?>) srvDirectory.getChild("HelloWorldService.java");
        InputStream sStream = ErraiPlugin.class.getResourceAsStream("/errai-bus/java/server/HelloWorldService.java.txt");
        serverIndexPage.setContents(Utils.replacePackageName(sStream,plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "HelloWorldService", "class"));

        //create App.gwt config file
        FileResource<?> confIndexPage = (FileResource<?>) sourceRoot.getChild("App.gwt.xml");
        InputStream cfStream = ErraiPlugin.class.getResourceAsStream("/errai-bus/java/App.gwt.xml.txt");
        confIndexPage.setContents(cfStream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "App.gwt.xml", "file"));
        
    }
    
    void createResourceFiles(final PipeOut pipeOut) {
        DirectoryResource sourceRoot = erraiExampleFacet.getResourceDirectory();
        
        //create App props
        FileResource<?> appIndexPage = (FileResource<?>) sourceRoot.getChild("ErraiApp.properties");
        InputStream stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/resources/ErraiApp.properties");
        appIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "ErraiApp.properties", "file"));
        
        //create Service props
        FileResource<?> serviceIndexPage = (FileResource<?>) sourceRoot.getChild("ErraiService.properties");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/resources/ErraiService.properties");
        serviceIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "ErraiService.properties", "file"));
        
        //create log4j props
        FileResource<?> log4jIndexPage = (FileResource<?>) sourceRoot.getChild("log4j.properties");
        stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/resources/log4j.properties");
        log4jIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "log4j.properties", "file"));
        
    }    
    
    void createTestFiles(final PipeOut pipeOut) {
        DirectoryResource resourceRoot = erraiExampleFacet.getTestResourceDirectory();
        
        //create App props
        FileResource<?> appIndexPage = (FileResource<?>) resourceRoot.getChild("ErraiApp.properties");
        InputStream stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/resources/ErraiApp.properties");
        appIndexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "ErraiApp.properties", "file"));
        
        DirectoryResource javaRoot = erraiExampleFacet.getTestBasePackageDirectory();
        
        DirectoryResource clDirectory = javaRoot.getOrCreateChildDirectory("client");
        clDirectory = clDirectory.getOrCreateChildDirectory("local");
        
        //create client class
        FileResource<?> cl1IndexPage = (FileResource<?>) clDirectory.getChild("ErraiIocTestHelper.java");
        InputStream c1Stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/test/ErraiIocTestHelper.java.txt");
        cl1IndexPage.setContents(Utils.replacePackageName(c1Stream, plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "ErraiIocTestHelper.java", "class"));
        
        //create client class
        FileResource<?> cl2IndexPage = (FileResource<?>) clDirectory.getChild("HelloWorldClientTest.java");
        InputStream c2Stream = ErraiPlugin.class.getResourceAsStream("/errai-bus/test/HelloWorldClientTest.java.txt");
        cl2IndexPage.setContents(Utils.replacePackageName(c2Stream,plugin.getProject()));
        pipeOut.println(ShellColor.YELLOW, String.format(ErraiBaseFacet.SUCCESS_MSG_FMT, "HelloWorldClientTest.java", "class"));
    }
}
