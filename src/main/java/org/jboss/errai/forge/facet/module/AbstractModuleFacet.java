package org.jboss.errai.forge.facet.module;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.constant.ModuleVault.Module;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.forge.shell.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

abstract class AbstractModuleFacet extends AbstractBaseFacet {

  /**
   * A collection of module logical names to be inherited (fully-qualified, not
   * "gwt.xml" suffix).
   */
  protected Collection<Module> modules;
  @Inject
  protected Shell shell;
  @Inject
  protected ProjectConfig config;

  final private static Properties xmlProperties = new Properties();
  {
    xmlProperties.setProperty(OutputKeys.INDENT, "yes");
    xmlProperties.setProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Google Inc.//DTD Google Web Toolkit 1.6//EN");
    xmlProperties.setProperty(OutputKeys.DOCTYPE_SYSTEM,
            "http://google-web-toolkit.googlecode.com/svn/releases/1.6/distro-source/core/src/gwt-module.dtd");
  }

  @Override
  public boolean install() {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      final DocumentBuilder builder = factory.newDocumentBuilder();
      final Document doc = builder.parse(getModuleFile());
      final Node root = doc.getElementsByTagName("module").item(0);
      final NodeList curModules = doc.getElementsByTagName("inherits");

      final Set<String> curModuleNames = new HashSet<String>();
      for (int i = 0; i < curModules.getLength(); i++) {
        curModuleNames.add(curModules.item(i).getAttributes().getNamedItem("name").getNodeValue());
      }

      Node before = null;
      for (final Module newModule : modules) {
        if (!curModuleNames.contains(newModule.getLogicalName())) {
          // Append after last insertion (or after last inherit tag)
          if (before == null) {
            final NodeList childNodes = root.getChildNodes();
            for (int j = childNodes.getLength() - 1; j >= 0; j--) {
              if (childNodes.item(j).getNodeName().equals("inherits")) {
                before = childNodes.item(j).getNextSibling();
                break;
              }
            }
          }

          final Element newNode = doc.createElement("inherits");
          newNode.setAttribute("name", newModule.getLogicalName());

          root.insertBefore(newNode, before);
        }
      }

      final TransformerFactory transFactory = TransformerFactory.newInstance();
      final Transformer transformer = transFactory.newTransformer();
      final DOMSource source = new DOMSource(doc);
      final StreamResult res = new StreamResult(getModuleFile());
      transformer.setOutputProperties(xmlProperties);
      transformer.transform(source, res);
    }
    catch (Exception e) {
      error("Error: failed to add required inheritance to module.", e);
      return false;
    }

    return true;
  }
  
  @Override
  public boolean isInstalled() {
    final File moduleFile = getModuleFile();
    if (moduleFile == null || !moduleFile.exists())
      return false;
    
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      final DocumentBuilder builder = factory.newDocumentBuilder();
      final Document doc = builder.parse(getModuleFile());
      final NodeList curModules = doc.getElementsByTagName("inherits");

      final Set<String> curModuleNames = new HashSet<String>();
      for (int i = 0; i < curModules.getLength(); i++) {
        curModuleNames.add(curModules.item(i).getAttributes().getNamedItem("name").getNodeValue());
      }
      
      for (final Module module : modules) {
        if (!curModuleNames.contains(module.getLogicalName()))
          return false;
      }
      
      return true;
    }
    catch (Exception e) {
      error("Error: could not read gwt module.", e);
      return false;
    }
  }
  
  @Override
  public boolean uninstall() {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      final DocumentBuilder builder = factory.newDocumentBuilder();
      final Document doc = builder.parse(getModuleFile());
      final NodeList curModules = doc.getElementsByTagName("inherits");

      final Set<String> moduleNames = new HashSet<String>(modules.size());
      for (final Module module : modules) {
        moduleNames.add(module.getLogicalName());
      }
      
      for (int i = 0; i < curModules.getLength(); i++) {
        final Node item = curModules.item(i);
        if (moduleNames.contains(item.getAttributes().getNamedItem("name").getTextContent())) {
          item.getParentNode().removeChild(item);
          i--;
        }
      }

      final TransformerFactory transFactory = TransformerFactory.newInstance();
      final Transformer transformer = transFactory.newTransformer();
      final DOMSource source = new DOMSource(doc);
      final StreamResult res = new StreamResult(getModuleFile());
      transformer.setOutputProperties(xmlProperties);
      transformer.transform(source, res);
      
      return true;
    }
    catch (Exception e) {
      error("Error: failed to remove inherited modules.", e);
      return false;
    }

  }

  protected void error(final String msg, final Exception ex) {
    shell.println(msg);
    if (shell.isVerbose() && ex != null) {
      for (final StackTraceElement trace : ex.getStackTrace()) {
        shell.println(trace.toString());
      }
    }
  }

  public File getModuleFile() {
    return config.getProjectProperty(ProjectProperty.MODULE, File.class);
  }

}
