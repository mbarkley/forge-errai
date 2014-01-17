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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
  protected final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
  protected final XPathFactory xPathFactory = XPathFactory.newInstance();

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
      final DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
      final Document doc = builder.parse(file);
      final XPath xPath = xPathFactory.newXPath();
      final Map<XPathExpression, Collection<Node>> toInsert = getElementsToInsert(xPath, doc);
      final Map<XPathExpression, Node> replacements = getReplacements(xPath, doc);

      for (final XPathExpression expression : toInsert.keySet()) {
        final Node node = (Node) expression.evaluate(doc, XPathConstants.NODE);
        if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
          for (final Node newNode : toInsert.get(expression)) {
            node.appendChild(newNode);
          }
        }
      }

      for (final XPathExpression expression : replacements.keySet()) {
        final Node node = (Node) expression.evaluate(doc, XPathConstants.NODE);
        if (node != null) {
          final Node parent = node.getParentNode();
          final Node newNode = replacements.get(expression);
          parent.replaceChild(newNode, node);
        }
      }

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
      final DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
      final Document doc = builder.parse(file);
      final XPath xPath = xPathFactory.newXPath();
      final Map<XPathExpression, Collection<Node>> insertedToCheck = getElementsToVerify(xPath, doc);
      final Map<XPathExpression, Node> replacedToCheck = getReplacements(xPath, doc);

      for (final XPathExpression expression : insertedToCheck.keySet()) {
        final Node parent = (Node) expression.evaluate(doc, XPathConstants.NODE);
        if (parent != null) {
          for (final Node inserted : insertedToCheck.get(expression)) {
            if (!hasMatchingChild(parent, inserted)) {
              return false;
            }
          }
        }
      }

      for (final XPathExpression expression : replacedToCheck.keySet()) {
        final Node replaced = (Node) expression.evaluate(doc, XPathConstants.NODE);
        if (replaced != null) {
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

  protected Node getMatchingChild(final Node parent, final Node inserted) {
    for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
      if (matches(inserted, parent.getChildNodes().item(i)))
        return parent.getChildNodes().item(i);
    }
    
    return null;
  }

  protected boolean hasMatchingChild(final Node parent, final Node inserted) {
    return getMatchingChild(parent, inserted) != null;
  }

  /**
   * Get a collection of nodes used to verify the installation of this facet.
   * Nodes in this collection must match nodes in the XML resource if this facet
   * is installed. If a subclass does not override this, the default value will
   * be the nodes returned by
   * {@link AbstractXmlResourceFacet#getElementsToInsert(Document)
   * getElementsToInsert}.
   */
  protected Map<XPathExpression, Collection<Node>> getElementsToVerify(final XPath xPath, final Document doc)
          throws ParserConfigurationException, XPathExpressionException {
    return getElementsToInsert(xPath, doc);
  }

  @Override
  public boolean uninstall() {
    final File file = getResFile(getRelPath());
    if (!file.exists())
      // XXX not sure if this case should return true or false...
      return true;

    try {
      final DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
      final Document doc = builder.parse(file);
      final XPath xPath = xPathFactory.newXPath();
      final Map<XPathExpression, Collection<Node>> insertedNodes = getElementsToInsert(xPath, doc);
      final Map<XPathExpression, Node> replacedNodes = getRemovalMap(xPath, doc);

      for (final XPathExpression expression : insertedNodes.keySet()) {
        final Node parent = (Node) expression.evaluate(doc, XPathConstants.NODE);
        if (parent != null) {
          for (final Node inserted : insertedNodes.get(expression)) {
            final Node match = getMatchingChild(parent, inserted);
            if (match != null) {
              parent.removeChild(match);
            }
          }
        }
      }

      for (final XPathExpression expression : replacedNodes.keySet()) {
        final Node replaced = (Node) expression.evaluate(doc, XPathConstants.NODE);
        if (replaced != null) {
          final Node parent = replaced.getParentNode();
          final Node newNode = replacedNodes.get(expression);
          parent.replaceChild(newNode, replaced);
        }
      }
    }
    catch (Exception e) {
      printError("Error occurred while attempting to verify xml resource " + file.getAbsolutePath(), e);
      return false;
    }

    return true;
  }

  protected abstract Map<XPathExpression, Node> getRemovalMap(XPath xPath, Document doc)
          throws ParserConfigurationException, XPathExpressionException;

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
   * @param xPath
   *          Used to generate xpath expressions for finding nodes to add
   *          children to.
   * @return A map of {@link XPathExpression XPathExpressions} (for finding
   *         parent nodes), to collections of {@link Node Nodes} to add as
   *         children.
   */
  protected abstract Map<XPathExpression, Collection<Node>> getElementsToInsert(final XPath xPath, final Document doc)
          throws ParserConfigurationException, XPathExpressionException;

  /**
   * Get a Map of nodes to be replaced in an XML configuration file.
   * 
   * @param doc
   *          The document to which all returned nodes should belong.
   * @param xPath
   *          Used to generate xpath expressions for finding nodes to replace.
   * @return A map of {@link XPathExpression XPathExpressions} (for finding
   *         nodes to replace), to collections of {@link Node Nodes} to add as
   *         children.
   */
  protected abstract Map<XPathExpression, Node> getReplacements(final XPath xPath, final Document doc)
          throws ParserConfigurationException, XPathExpressionException;

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