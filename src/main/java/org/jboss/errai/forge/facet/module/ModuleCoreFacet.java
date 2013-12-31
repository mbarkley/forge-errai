package org.jboss.errai.forge.facet.module;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.constant.ModuleVault.Module;
import org.jboss.forge.shell.Shell;

public class ModuleCoreFacet extends AbstractModuleFacet {

  private final Shell shell;

  public ModuleCoreFacet(final Shell shell) {
    this();
    this.shell = shell;
  }
  
  public ModuleCoreFacet() {
    modules = Arrays.asList(new Module[] {
            Module.GwtUser
    });
  }

  @Override
  public boolean install() {
    final File module = ProjectConfig.getMainConfig().getProjectProperty(ProjectProperty.MODULE, File.class);
    if (!module.exists()) {
      module.getParentFile().mkdirs();
      try {
        module.createNewFile();
      }
      catch (IOException e) {
        error("Could not create module at " + module.getAbsolutePath(), e);
        return false;
      }

      final FileWriter writer;
      try {
        writer = new FileWriter(module);
        writer.append("<module></module>");
      }
      catch (IOException e) {
        error("Cannot write to module at " + module.getAbsolutePath(), e);
        return false;
      }
      
      try {
        if (writer != null)
          writer.close();
      }
      catch (IOException e) {
        shell.println("warning: could not close module file " + module.getAbsolutePath());
      }

    }

    return super.install();
  }

  private void error(final String msg, final Exception ex) {
    shell.println(msg);
    if (shell.isVerbose() && ex != null) {
      for (final StackTraceElement trace : ex.getStackTrace()) {
        shell.println(trace.toString());
      }
    }
  }

}
