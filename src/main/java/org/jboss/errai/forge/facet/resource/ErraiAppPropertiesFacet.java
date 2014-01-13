package org.jboss.errai.forge.facet.resource;

public class ErraiAppPropertiesFacet extends AbstractFileResourceFacet {
  
  public ErraiAppPropertiesFacet() {
    relFilePath = "src/main/resources/ErraiApp.properties";
  }

  @Override
  protected String getResourceContent() throws Exception {
    return "";
  }

}
