package org.jboss.errai.forge.facet.resource;

public class WebXmlFacet extends AbstractFileResourceFacet {
  
  private final String templateFile = "template-web.xml";
  
  public WebXmlFacet() {
    relFilePath = "src/main/webapp/WEB-INF/web.xml";
  }

  @Override
  protected String getResourceContent() throws Exception {
    return readResource(templateFile).toString();
  }

}
