package org.jboss.errai.forge.facet.resource;

public class BeansXmlFacet extends AbstractFileResourceFacet {
  
  private final String templateResource = "template-beans.xml";
  
  public BeansXmlFacet() {
    relFilePath = "src/main/webapp/WEB-INF/beans.xml";
  }

  @Override
  protected String getResourceContent() throws Exception {
    return readResource(templateResource).toString();
  }

}
