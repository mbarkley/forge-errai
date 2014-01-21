package org.jboss.errai.forge.facet.resource;

public class ConfigXmlFacet extends AbstractFileResourceFacet {
  
  public ConfigXmlFacet() {
    relFilePath = "src/main/webapp/config.xml";
  }

  @Override
  protected String getResourceContent() throws Exception {
    return readResource(relFilePath).toString();
  }

}
