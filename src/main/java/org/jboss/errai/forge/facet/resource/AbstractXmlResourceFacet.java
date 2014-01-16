package org.jboss.errai.forge.facet.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
 * A base class for modifying XML-based configuration files. Concrete subclasses
 * may modify the field {@link AbstractXmlResourceFacet#xmlProperties
 * xmlProperties}.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class AbstractXmlResourceFacet extends AbstractBaseFacet {

  /**
   * The properties used when writing to an XML file.
   * 
   * @see {@link OutputKeys}, {@link Transformer}
   */
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
      final Map<Element, Element> replacements = getReplacements(doc);
      final Node root = doc.getDocumentElement();
      final Map<String, Collection<Node>> toInsertByTagName = mapElementsByTagName(toInsert);

      doReplacements(doc, replacements);
      safeBatchAdd(root, toInsertByTagName);

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

  private void doReplacements(final Document doc, final Map<Element, Element> replacements) {
    for (final Entry<Element, Element> mapping : replacements.entrySet()) {
      final NodeList nodeList = doc.getElementsByTagName(mapping.getKey().getNodeName());
      for (int i = 0; i < nodeList.getLength(); i++) {
        if (matches(mapping.getKey(), nodeList.item(i))) {
          final Node parent = nodeList.item(i).getParentNode();
          if (mapping.getValue() != null)
            parent.replaceChild(mapping.getValue(), nodeList.item(i));
          else
            parent.removeChild(nodeList.item(i));
          break;
        }
      }
    }
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
            if (node.getNodeType() == Node.ELEMENT_NODE && matches(node, existing))
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
            if (node.getNodeType() == Node.ELEMENT_NODE && matches(node, existing)) {
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

  /**
   * Get DOM elements to write to an XML configuration file. Concrete subclasses
   * should return a collection of nodes that will be merged as children of the
   * root element of the specified XML file.
   * 
   * @param doc
   *          The returned nodes should be created with or imported into this
   *          document.
   * @return A collection of nodes, which are part of the given document.
   */
  protected abstract Collection<Node> getElementsToInsert(final Document doc) throws ParserConfigurationException;

  /**
   * Get a Map of nodes to be replaced in an XML configuration file.
   * 
   * @param doc
   *          The document to which all returned nodes should belong.
   * @return A map where any keys appearing in an XML file will be replaced with
   *         the corresponding values.
   */
  protected abstract Map<Element, Element> getReplacements(final Document doc) throws ParserConfigurationException;

  /**
   * Get the relative path of XML file to be configured by this facet. Concrete
   * subclasses must return the path (relative to the project root directory) of
   * the XML file they are configuring.
   * 
   * @return The path (relative to the project root directory) of an XML
   *         configuration file.
   */
  protected abstract String getRelPath();

  /**
   * @return A map with key values of node names, mapping to collections of
   *         nodes with the corresponding node name.
   */
  protected Map<String, Collection<Node>> mapElementsByTagName(Collection<Node> elements) {
    final Map<String, Collection<Node>> retVal = new HashMap<String, Collection<Node>>();
    for (final Node node : elements) {
      if (!retVal.containsKey(node.getNodeName()))
        retVal.put(node.getNodeName(), new ArrayList<Node>());
      retVal.get(node.getNodeName()).add(node);
    }

    return retVal;
  }

  /**
   * @return A map with key values of node names, mapping to collections of
   *         nodes with the corresponding node name.
   */
  protected Map<String, Collection<Node>> mapElementsByTagName(final NodeList nodes) {
    final List<Node> list = new ArrayList<Node>(nodes.getLength());
    for (int i = 0; i < nodes.getLength(); i++) {
      list.add(nodes.item(i));
    }

    return mapElementsByTagName(list);
  }

  /**
   * Add a collection of nodes as children of another node. If a node in the
   * collection matches a child of the parent node, do not add it.
   * 
   * @param parent
   *          The node to add children to.
   * @param namedNodes
   *          A collection of nodes to add as children.
   */
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
          if (node.getNodeType() == Node.ELEMENT_NODE && matches(node, existingNode))
            break nodeLoop;
        }
        Node before = null;
        if (!existing.isEmpty())
          before = existing.get(existing.size() - 1).getNextSibling();
        parent.insertBefore(node, before);
      }
    }
  }

  /**
   * Check if a node is consistent with another. A node, {@code other} is
   * consistent with another node, {@code node}, if the tree rooted at
   * {@code node} is a subtree of {@code other} (i.e. every child element,
   * attribute, or text value in {@code node} exists in the same relative path
   * in {@code other}).
   * 
   * @param node
   *          The primary node for matching against.
   * @param other
   *          The secondary node being matched against the primary node.
   * @return True iff {@code other} is consistent with {@code node}.
   */
  protected boolean matches(Node node, Node other) {
    if (!(other instanceof Element)) {
      return false;
    }
    if (!(node instanceof Element)) {
      throw new IllegalArgumentException("Arguments must be instances of Element.");
    }

    final Element e1 = (Element) node, e2 = (Element) other;
    if (!e1.getNodeName().equals(e2.getNodeName()))
      return false;

    // other must have attributes consistent with node
    final NamedNodeMap attributes = e1.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      final Node item = attributes.item(i);
      if (!e2.hasAttribute(item.getNodeName()) || !e2.getAttribute(item.getNodeName()).equals(item.getNodeValue()))
        return false;
    }

    // children of other must be consistent with children of node
    if (e1.hasChildNodes()) {
      outer: for (Node child = e1.getFirstChild(); child != null; child = child.getNextSibling()) {
        if (!(child instanceof Element))
          continue;
        for (Node otherChild = e2.getFirstChild(); otherChild != null; otherChild = otherChild.getNextSibling()) {
          if (otherChild instanceof Element && matches(child, otherChild))
            continue outer;
        }

        return false;
      }
    }

    return true;
  }
}