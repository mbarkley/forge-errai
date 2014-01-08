package org.jboss.errai.forge.facet.resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
  
  protected StringBuilder readResource(final String resource) throws IOException {
    final StringBuilder builder = new StringBuilder();
    final InputStream stream = getClass().getResourceAsStream(resource);
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
    
    return builder;
  }

  /**
   * Replace all occurrences of a {@link String} in a give {@link StringBuilder}.
   * 
   * @param subject The {@link StringBuilder} to be modified.
   * @param toReplace The {@link String} to be replaced.
   * @param replacement The replacement {@link String}.
   */
  protected void replace(final StringBuilder subject, final String toReplace, final String replacement) {
    for (int fillerIndex = subject.indexOf(toReplace); fillerIndex > -1; fillerIndex = subject.indexOf(toReplace,
            fillerIndex)) {
      subject.replace(fillerIndex, fillerIndex + toReplace.length(), replacement);
    }
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
