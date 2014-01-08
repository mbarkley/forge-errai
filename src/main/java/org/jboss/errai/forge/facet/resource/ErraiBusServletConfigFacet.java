package org.jboss.errai.forge.facet.resource;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.jboss.forge.shell.plugins.RequiresFacet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@RequiresFacet({ WebXmlFacet.class })
public class ErraiBusServletConfigFacet extends AbstractXmlResourceFacet {

  @Override
  protected Collection<Node> getElementsToInsert(Document doc) throws ParserConfigurationException {
    final Element servlet = doc.createElement("servlet");
    servlet.appendChild(doc.createElement("servlet-name")).setTextContent("ErraiServlet");
    servlet.appendChild(doc.createElement("sevlet-class")).setTextContent(
            "org.jboss.errai.bus.server.servlet.DefaultBlockingServlet");
    
    final Node initParam = servlet.appendChild(doc.createElement("init-param"));
    initParam.appendChild(doc.createElement("param-name")).setTextContent("auto-discover-services");
    initParam.appendChild(doc.createElement("param-value")).setTextContent("true");
    
    servlet.appendChild(doc.createElement("load-on-startup")).setTextContent("1");
    
    final Element servletMapping = doc.createElement("servlet-mapping");
    servletMapping.appendChild(doc.createElement("servlet-name")).setTextContent("ErraiServlet");
    servletMapping.appendChild(doc.createElement("url-pattern")).setTextContent("*.erraiBus");
    
    return Arrays.asList(new Node[] {
            servlet,
            servletMapping
    });
  }

  @Override
  protected String getRelPath() {
    return "src/main/webapp/WEB-INF/web.xml";
  }

}