package org.jboss.errai.forge.facet.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.ProjectConfigFactory;
import org.jboss.errai.forge.constant.ModuleVault.Module;
import org.jboss.errai.forge.facet.resource.AbstractXmlResourceFacet;
import org.jboss.forge.shell.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

abstract class AbstractModuleFacet extends AbstractXmlResourceFacet {

  /**
   * A collection of module logical names to be inherited (fully-qualified, not
   * "gwt.xml" suffix).
   */
  protected Collection<Module> modules;
  @Inject
  protected Shell shell;
  @Inject
  protected ProjectConfigFactory configFactory;

  protected Collection<Node> generateInsertElements(final Collection<Module> modules, final Document doc)
          throws ParserConfigurationException {
    final Collection<Node> retVal = new ArrayList<Node>(modules.size());
    for (final Module mod : modules) {
      final Element elem = doc.createElement("inherits");
      elem.setAttribute("name", mod.getLogicalName());
      retVal.add(elem);
    }

    return retVal;
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
      printError("Error: failed to remove inherited modules.", e);
      return false;
    }
  }

  @Override
  protected Collection<Node> getElementsToInsert(Document doc) throws ParserConfigurationException {
    return generateInsertElements(modules, doc);
  }

  public File getModuleFile() {
    return configFactory.getProjectConfig(getProject()).getProjectProperty(ProjectProperty.MODULE_FILE, File.class);
  }

  @Override
  protected String getRelPath() {
    return configFactory.getProjectConfig(getProject()).getProjectProperty(ProjectProperty.MODULE_FILE, File.class)
            .getPath();
  }

}
