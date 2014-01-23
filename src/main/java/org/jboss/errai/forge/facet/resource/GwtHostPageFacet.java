package org.jboss.errai.forge.facet.resource;

import java.io.File;

import javax.inject.Inject;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.ProjectConfigFactory;

/**
 * This facet adds a GWT host page if none already exists.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class GwtHostPageFacet extends AbstractFileResourceFacet {

  private final String templateName = "host_page_template.html";
  private final String FILLER_VALUE = "$$_MODULE_JS_FILE_$$";

  @Inject
  private ProjectConfigFactory configFactory;

  @Override
  protected String getResourceContent() throws Exception {
    final StringBuilder builder = readResource(templateName);
    final ProjectConfig config = configFactory.getProjectConfig(getProject());
    final String moduleName = config.getProjectProperty(ProjectProperty.MODULE_NAME, String.class);
    // Replace filler with actual module js file
    replace(builder, FILLER_VALUE, getJsFilePath(moduleName));

    return builder.toString();
  }

  private String getJsFilePath(final String moduleName) {
    return moduleName + File.separator + moduleName + ".nocache.js";
  }

  @Override
  public String getRelFilePath() {
    return "src/main/webapp/index.html";
  }

}
