package org.jboss.errai.forge.facet.resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jboss.errai.forge.facet.base.AbstractBaseFacet;

/**
 * Base class for facets that add required resource file (such as a .properties
 * file).
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
abstract class AbstractFileResourceFacet extends AbstractBaseFacet {

  /**
   * Relative to project root.
   */
  protected String relFilePath;

  @Override
  public boolean install() {
    final File file = getAbsoluteFilePath();
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      try {
        file.createNewFile();
      }
      catch (IOException e) {
        printError(getClass().getSimpleName() + ": Could not make the file " + file.getAbsolutePath(), e);
        return false;
      }
    }
    FileWriter writer = null;
    try {
      writer = new FileWriter(file);
      writer.write(getResourceContent());
    }
    catch (IOException e) {
      printError("Could not write to " + file.getAbsolutePath(), e);
      return false;
    }
    catch (Exception e) {
      printError("Unexpected error while trying to add resource " + relFilePath, e);
      return false;
    }
    finally {
      try {
        if (writer != null)
          writer.close();
      }
      catch (IOException e) {
        printError("Could not close FileWriter for " + file.getAbsolutePath(), e);
      }
    }

    return true;
  }

  protected abstract String getResourceContent() throws Exception;

  @Override
  public boolean isInstalled() {
    /*
     * Just check that the resource exists. Classes with more particular
     * requirements can override this.
     */
    return getAbsoluteFilePath().exists();
  }

  protected File getAbsoluteFilePath() {
    return new File(getProject().getProjectRoot().getUnderlyingResourceObject(), relFilePath);
  }

}
