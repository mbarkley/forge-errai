package org.jboss.errai.forge.facet.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * For adding and removing configurations from an xml file.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class AbstractXmlResourceFacet extends AbstractBaseFacet {

  final protected Properties xmlProperties = new Properties();
  protected final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  public AbstractXmlResourceFacet() {
    xmlProperties.setProperty(OutputKeys.INDENT, "yes");
  }

  @Override
  public boolean install() {
    try {
      final File file = getResFile(getRelPath());
      if (!file.exists()) {
        file.getParentFile().mkdirs();
        file.createNewFile();
      }
      final DocumentBuilder builder = factory.newDocumentBuilder();
      final Document doc = builder.parse(file);
      final Collection<Node> toInsert = getElementsToInsert(doc);
      final Node root = doc.getDocumentElement();
      final Map<String, Collection<Node>> elementsByTagName = mapElementsByTagName(toInsert);

      safeBatchAdd(root, elementsByTagName);

      final TransformerFactory transFactory = TransformerFactory.newInstance();
      final Transformer transformer = transFactory.newTransformer();
      final DOMSource source = new DOMSource(doc);
      final StreamResult res = new StreamResult(file);
      transformer.setOutputProperties(xmlProperties);
      transformer.transform(source, res);
    }
    catch (Exception e) {
      printError("Error: failed to add required inheritance to module.", e);
      return false;
    }

    return true;
  }

  @Override
  public boolean isInstalled() {
    final String relPath = getRelPath();
    if (relPath == null)
      // Project config has not been setup yet.
      return false;
    
    final File file = getResFile(relPath);
    if (!file.exists())
      return false;

    try {
      final DocumentBuilder builder = factory.newDocumentBuilder();
      final Document doc = builder.parse(file);
      final Collection<Node> toCheck = getElementsToInsert(doc);
      final Element root = doc.getDocumentElement();
      final Map<String, Collection<Node>> elementsByTagName = mapElementsByTagName(toCheck);
      final Map<String, Collection<Node>> existingByTagName = mapElementsByTagName(root.getChildNodes());
      
      for (final String name : elementsByTagName.keySet()) {
        if (!existingByTagName.containsKey(name))
          return false;
        
        findLoop: for (final Node node : elementsByTagName.get(name)) {
          for (final Node existing : existingByTagName.get(name)) {
            if (matches(node, existing))
              continue findLoop;
          }
          return false;
        }
      }
    }
    catch (Exception e) {
      printError("Error occurred while attempting to verify xml resource " + file.getAbsolutePath(), e);
      return false;
    }

    return true;
  }

  @Override
  public boolean uninstall() {
    final File file = getResFile(getRelPath());
    if (!file.exists())
      // XXX not sure if this case should return true or false...
      return true;

    try {
      final DocumentBuilder builder = factory.newDocumentBuilder();
      final Document doc = builder.parse(file);
      final Collection<Node> toRemove = getElementsToInsert(doc);
      final Element root = doc.getDocumentElement();
      final Map<String, Collection<Node>> elementsByTagName = mapElementsByTagName(toRemove);
      final Map<String, Collection<Node>> existingByTagName = mapElementsByTagName(root.getChildNodes());
      
      for (final String name : elementsByTagName.keySet()) {
        if (!existingByTagName.containsKey(name))
          continue;
        
        for (final Node node : elementsByTagName.get(name)) {
          for (final Node existing : existingByTagName.get(name)) {
            if (matches(node, existing)) {
              existing.getParentNode().removeChild(existing);
            }
          }
        }
      }
    }
    catch (Exception e) {
      printError("Error occurred while attempting to verify xml resource " + file.getAbsolutePath(), e);
      return false;
    }

    return true;
  }

  protected File getResFile(final String relPath) {
    File file = new File(relPath);
    if (!file.isAbsolute())
      file = new File(getProject().getProjectRoot().getUnderlyingResourceObject(), file.getPath());

    return file;
  }

  protected abstract Collection<Node> getElementsToInsert(final Document doc) throws ParserConfigurationException;

  protected abstract String getRelPath();

  protected Map<String, Collection<Node>> mapElementsByTagName(Collection<Node> elements) {
    final Map<String, Collection<Node>> retVal = new HashMap<String, Collection<Node>>();
    for (final Node node : elements) {
      if (!retVal.containsKey(node.getNodeName()))
        retVal.put(node.getNodeName(), new ArrayList<Node>());
      retVal.get(node.getNodeName()).add(node);
    }

    return retVal;
  }
  
  protected Map<String, Collection<Node>> mapElementsByTagName(final NodeList nodes) {
    final List<Node> list = new ArrayList<Node>(nodes.getLength());
    for (int i = 0; i < nodes.getLength(); i++) {
      list.add(nodes.item(i));
    }
    
    return mapElementsByTagName(list);
  }

  protected void safeBatchAdd(final Node parent, final Map<String, Collection<Node>> namedNodes) {
    Map<String, List<Node>> existingMap = new HashMap<String, List<Node>>();
    for (final String name : namedNodes.keySet()) {
      existingMap.put(name, new ArrayList<Node>());
    }
    for (Node cur = parent.getFirstChild(); cur != null; cur = cur.getNextSibling()) {
      if (namedNodes.containsKey(cur.getNodeName())) {
        existingMap.get(cur.getNodeName()).add(cur);
      }
    }
    for (final String name : namedNodes.keySet()) {
      List<Node> existing = existingMap.get(name);
      nodeLoop: for (final Node node : namedNodes.get(name)) {
        for (final Node existingNode : existing) {
          if (matches(node, existingNode))
            break nodeLoop;
        }
        Node before = null;
        if (!existing.isEmpty())
          before = existing.get(existing.size() - 1).getNextSibling();
        parent.insertBefore(node, before);
      }
    }
  }

  protected boolean matches(Node node, Node other) {
    if (!(node instanceof Element) || !(other instanceof Element)) {
      throw new IllegalArgumentException("Arguments must be instances of Element.");
    }

    final Element e1 = (Element) node, e2 = (Element) other;
    if (!e1.getNodeName().equals(e2.getNodeName()))
      return false;

    final NamedNodeMap attributes = e1.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      final Node item = attributes.item(i);
      if (!e2.hasAttribute(item.getNodeName()) || !e2.getAttribute(item.getNodeName()).equals(item.getNodeValue()))
        return false;
    }

    return true;
  }
}