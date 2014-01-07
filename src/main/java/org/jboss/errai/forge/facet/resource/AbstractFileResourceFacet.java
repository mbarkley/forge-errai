package org.jboss.errai.forge.facet.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
  /**
   * Path to a resource in classpath.
   */
  protected String resourcePath;

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
    FileOutputStream writer = null;
    final InputStream stream = getClass().getResourceAsStream(resourcePath);
    try {
      writer = new FileOutputStream(file);
      final byte[] buf = new byte[256];
      int amtRead;
      while (true) {
        amtRead = stream.read(buf);
        if (amtRead == -1)
          break;
        else
          writer.write(buf, 0, amtRead);
      }
    }
    catch (IOException e) {
      printError("Could not write to " + file.getAbsolutePath(), e);
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
      try {
        stream.close();
      }
      catch (IOException e) {
        printError("Could not close resource stream " + resourcePath, e);
      }
    }

    return true;
  }

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
