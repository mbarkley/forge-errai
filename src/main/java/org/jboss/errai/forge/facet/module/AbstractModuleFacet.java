package org.jboss.errai.forge.facet.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

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
  protected Collection<Node> getElementsToInsert(Document doc) throws ParserConfigurationException {
    return generateInsertElements(modules, doc);
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
