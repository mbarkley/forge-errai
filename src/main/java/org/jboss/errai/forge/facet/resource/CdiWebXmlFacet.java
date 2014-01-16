package org.jboss.errai.forge.facet.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.jboss.forge.shell.plugins.RequiresFacet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This facet sets
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ ErraiBusServletConfigFacet.class })
public class CdiWebXmlFacet extends AbstractXmlResourceFacet {

  @Override
  protected Collection<Node> getElementsToInsert(Document doc) throws ParserConfigurationException {
    return new ArrayList<Node>(0);
  }

  @Override
  protected Map<Element, Element> getReplacements(Document doc) throws ParserConfigurationException {
    final Element key = doc.createElement("init-param");
    key.appendChild(doc.createElement("param-name")).setTextContent("auto-discover-services");
    key.appendChild(doc.createElement("param-value")).setTextContent("true");

    final Element value = doc.createElement("init-param");
    value.appendChild(doc.createElement("param-name")).setTextContent("auto-discover-services");
    value.appendChild(doc.createElement("param-value")).setTextContent("false");

    final Map<Element, Element> replacements = new HashMap<Element, Element>(1);
    replacements.put(key, value);

    return replacements;
  }

  @Override
  protected String getRelPath() {
    return "src/main/webapp/WEB-INF/web.xml";
  }

}
