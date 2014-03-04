package org.jboss.errai.forge.facet.resource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.jboss.errai.forge.facet.plugin.WarPluginFacet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class SecurityBeansXmlFacet extends AbstractXmlResourceFacet {

  @Override
  protected Map<XPathExpression, Node> getRemovalMap(final XPath xPath, final Document doc)
          throws ParserConfigurationException,
          XPathExpressionException {
    return new HashMap<XPathExpression, Node>(0);
  }

  @Override
  public boolean install() {
    try {
      final File file = new File(getRelPath());
      final Document doc = docBuilderFactory.newDocumentBuilder().parse(file);
      final XPathExpression expression = xPathFactory.newXPath().compile("/beans/interceptors");
      Node node = (Node) expression.evaluate(doc, XPathConstants.NODE);
      if (node == null) {
        node = doc.createElement("interceptors");
        doc.insertBefore(node, null);
        writeDocument(doc, file);
      }
    }
    catch (SAXException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    catch (XPathExpressionException e) {
      e.printStackTrace();
    }
    catch (TransformerException e) {
      e.printStackTrace();
    }

    return super.install();
  }

  @Override
  protected Map<XPathExpression, Collection<Node>> getElementsToInsert(final XPath xPath, final Document doc)
          throws ParserConfigurationException, XPathExpressionException {
    final Map<XPathExpression, Collection<Node>> retVal = new HashMap<XPathExpression, Collection<Node>>();
    final Element userInterceptor = doc.createElement("class");
    userInterceptor.setTextContent("org.jboss.errai.security.server.SecurityUserInterceptor");
    final Element roleInterceptor = doc.createElement("class");
    roleInterceptor.setTextContent("org.jboss.errai.security.server.ServerSecurityRoleInterceptor");

    retVal.put(xPath.compile("/beans/interceptors"), Arrays.asList(new Node[] {
        userInterceptor,
        roleInterceptor
    }));

    return retVal;
  }

  @Override
  protected Map<XPathExpression, Node> getReplacements(final XPath xPath, final Document doc)
          throws ParserConfigurationException,
          XPathExpressionException {
    return new HashMap<XPathExpression, Node>(0);
  }

  @Override
  protected String getRelPath() {
    return WarPluginFacet.getWarSourceDirectory(getProject()) + "/WEB-INF/beans.xml";
  }

}
