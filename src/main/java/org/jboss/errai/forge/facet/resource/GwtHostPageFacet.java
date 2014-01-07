package org.jboss.errai.forge.facet.resource;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.ProjectConfigFactory;

public class GwtHostPageFacet extends AbstractFileResourceFacet {

  private final String templateName = "host_page_template.html";
  private final String FILLER_VALUE = "$$_MODULE_JS_FILE_$$";
  
  @Inject
  private ProjectConfigFactory configFactory;

  public GwtHostPageFacet() {
    relFilePath = "src/main/webapp/index.html";
  }

  @Override
  protected String getResourceContent() throws Exception {
    final StringBuilder builder = new StringBuilder();
    final InputStream stream = getClass().getResourceAsStream(templateName);
    final InputStreamReader reader = new InputStreamReader(stream);
    final char[] buf = new char[256];
    int read;
    while (true) {
      read = reader.read(buf);
      if (read == -1)
        break;
      builder.append(buf, 0, read);
    }
    reader.close();
    stream.close();
    
    final ProjectConfig config = configFactory.getProjectConfig(getProject());
    final String moduleName = config.getProjectProperty(ProjectProperty.MODULE_LOGICAL, String.class);
    // Replace filler with actual module js file
    int fillerIndex = builder.indexOf(FILLER_VALUE);
    builder.replace(fillerIndex, fillerIndex + FILLER_VALUE.length(), getJsFilePath(moduleName));

    return builder.toString();
  }
  
  private String getJsFilePath(final String moduleName) {
    return moduleName + File.separator + moduleName + ".nocache.js";
  }

}
