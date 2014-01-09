package org.jboss.errai.forge.facet.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.forge.project.Project;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AbstractXmlResourceFacetTest extends AbstractShellTest {

  public static class TestXmlResourceFacet extends AbstractXmlResourceFacet {
    private final String relPath;
    private final Collection<Node> nodes;

    public TestXmlResourceFacet(final String relPath, final Collection<Node> nodes) {
      this.relPath = relPath;
      this.nodes = nodes;
    }

    @Override
    protected Collection<Node> getElementsToInsert(Document doc) throws ParserConfigurationException {
      final Collection<Node> retVal = new ArrayList<Node>();
      for (final Node node : nodes) {
        retVal.add(doc.importNode(node, true));
      }

      return retVal;
    }

    @Override
    protected String getRelPath() {
      return relPath;
    }
  }

  @Test
  public void testIsInstalledRecursiveCase() throws Exception {
    final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    final List<Node> nodes = Arrays.asList(new Node[] { doc.createElement("first"), doc.createElement("first") });
    nodes.get(0).appendChild(doc.createElement("second")).appendChild(doc.createElement("third"));
    nodes.get(0).getFirstChild().appendChild(doc.createElement("fourth"));
    nodes.get(0).appendChild(doc.createElement("fifth"));
    ((Element) nodes.get(1).appendChild(doc.createElement("second"))).setAttribute("name", "test");

    final Project project = initializeJavaProject();
    final TestXmlResourceFacet testFacet = new TestXmlResourceFacet(
            writeResourceToFile("AbstractXmlResourceFacetTest-1.xml"), nodes);
    testFacet.setProject(project);

    assertTrue(testFacet.isInstalled());
  }

  @Test
  public void testNotInstalledRecursiveCase() throws Exception {
    final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    final List<Node> nodes = Arrays.asList(new Node[] { doc.createElement("first"), doc.createElement("first") });
    nodes.get(0).appendChild(doc.createElement("second")).appendChild(doc.createElement("third"));
    // This extra attribute should cause isInstalled to return false.
    ((Element) nodes.get(0).getFirstChild()).setAttribute("foo", "bar");
    nodes.get(0).getFirstChild().appendChild(doc.createElement("fourth"));
    nodes.get(0).appendChild(doc.createElement("fifth"));
    ((Element) nodes.get(1).appendChild(doc.createElement("second"))).setAttribute("name", "test");

    final Project project = initializeJavaProject();
    final TestXmlResourceFacet testFacet = new TestXmlResourceFacet(
            writeResourceToFile("AbstractXmlResourceFacetTest-1.xml"), nodes);
    testFacet.setProject(project);

    assertFalse(testFacet.isInstalled());
  }

  private String writeResourceToFile(final String res) throws IOException {
    final File file = File.createTempFile(getClass().getSimpleName(), ".xml");
    file.deleteOnExit();
    final BufferedInputStream stream = new BufferedInputStream(getClass().getResourceAsStream(res));
    final BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

    final byte[] buf = new byte[256];
    for (int read = stream.read(buf); read != -1; read = stream.read(buf)) {
      writer.write(buf, 0, read);
    }

    writer.close();
    stream.close();

    return file.getAbsolutePath();
  }

}
