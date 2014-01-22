package org.jboss.errai.forge.util;

import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.PipeOut;

public class ShellPrintFormatter {

  public static void printError(final PipeOut out, final String message) {
    out.print(ShellColor.RED, "Error: ");
    out.println(message);
  }
  
  public static void printTitle(final PipeOut out, final String title, final int width) {
    out.print(ShellColor.BOLD, padTitle(title, width));
  }
  
  private static String padTitle(final String title, final int width) {
    final StringBuilder builder = new StringBuilder(width);
    
    for (int i = 0; i < title.length(); i++) {
      builder.append(title.charAt(i));
    }
    for (int j = title.length(); j < width; j++) {
      builder.append(' ');
    }
    
    return builder.toString();
  }

}
