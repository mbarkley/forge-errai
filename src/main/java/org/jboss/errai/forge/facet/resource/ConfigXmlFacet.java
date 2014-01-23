package org.jboss.errai.forge.facet.resource;

public class ConfigXmlFacet extends AbstractFileResourceFacet {

  @Override
  protected String getResourceContent() throws Exception {
    return readResource(getRelFilePath()).toString();
  }

  @Override
  public String getRelFilePath() {
    return "src/main/webapp/config.xml";
  }

}
