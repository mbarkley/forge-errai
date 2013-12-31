package org.jboss.errai.forge.facet.module;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
   * A collection of module logical names to be inherited (fully-qualified, not "gwt.xml" suffix).
   */
  protected Collection<Module> modules;
  @Inject
  protected Shell shell;

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
    }
    catch (Exception e) {
      error("Error: failed to add required inheritance to module.", e);
      return false;
    }
    
    return true;
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
    return ProjectConfig.getMainConfig().getProjectProperty(ProjectProperty.MODULE, File.class);
  }

}
