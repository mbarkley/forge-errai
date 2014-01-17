package org.jboss.errai.forge.facet.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.ProjectConfigFactory;
import org.jboss.errai.forge.constant.ModuleVault.Module;
import org.jboss.errai.forge.facet.resource.AbstractXmlResourceFacet;
import org.jboss.forge.shell.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A base facet for facets which must add GWT module dependencies via the
 * "inherit" tag. Concrete subclasses must assign a {@link Collection} of
 * {@link Module Modules} they wish to inherit to
 * {@link AbstractModuleFacet#modules}.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
abstract class AbstractModuleFacet extends AbstractXmlResourceFacet {

  /**
   * A collection of GWT modules to inherit in this project.
   */
  protected Collection<Module> modules;
  @Inject
  protected Shell shell;
  @Inject
  protected ProjectConfigFactory configFactory;
  
  protected final String xPathRootExpression = "/module";

  /**
   * Generate xml {@link Element Elements} for inheriting the given {@link Module Modules}.
   * 
   * @param modules The modules to generate {@link Element Elements} for.
   * @param doc Used to generate the {@link Element Elements}.
   */
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
  protected Map<XPathExpression, Node> getRemovalMap(final XPath xPath, final Document doc) {
    return new HashMap<XPathExpression, Node>(0);
  }

  @Override
  protected Map<XPathExpression, Collection<Node>> getElementsToInsert(final XPath xPath, final Document doc)
          throws ParserConfigurationException, XPathExpressionException {
    final XPathExpression parentExpression = xPath.compile(xPathRootExpression);
    final Collection<Node> nodes = new ArrayList<Node>(modules.size());
    for (final Module module : modules) {
      final Element inherits = doc.createElement("inherits");
      inherits.setAttribute("name", module.getLogicalName());
      nodes.add(inherits);
    }
    
    final Map<XPathExpression, Collection<Node>> retVal = new HashMap<XPathExpression, Collection<Node>>(1);
    retVal.put(parentExpression, nodes);
    
    return retVal;
  }

  @Override
  protected Map<XPathExpression, Node> getReplacements(final XPath xPath, final Document doc) throws ParserConfigurationException {
    return new HashMap<XPathExpression, Node>(0);
  }

  /**
   * @return The absolute path of the GWT module used in this project.
   */
  public File getModuleFile() {
    return configFactory.getProjectConfig(getProject()).getProjectProperty(ProjectProperty.MODULE_FILE, File.class);
  }

  @Override
  protected String getRelPath() {
    final File module = configFactory.getProjectConfig(getProject()).getProjectProperty(ProjectProperty.MODULE_FILE,
            File.class);
    if (module != null)
      return module.getPath();
    else
      return null;
  }

}
