package org.jboss.errai.forge.facet.module;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.transform.OutputKeys;

import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.constant.ModuleVault.Module;
import org.jboss.forge.shell.Shell;

public class ModuleCoreFacet extends AbstractModuleFacet {

  final static String emptyModuleContents =
          "<?xml version='1.0' encoding='UTF-8'?>\n"
          + "<!DOCTYPE module PUBLIC '-//Google Inc.//DTD Google Web Toolkit 1.6//EN'\n\t"
          + "'http://google-web-toolkit.googlecode.com/svn/releases/1.6/distro-source/core/src/gwt-module.dtd'>\n"
          + "<module></module>\n";

  public ModuleCoreFacet(final Shell shell) {
    this();
    this.shell = shell;
  }
  
  public ModuleCoreFacet() {
    modules = Arrays.asList(new Module[] {
            Module.GwtUser
    });
    xmlProperties.setProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Google Inc.//DTD Google Web Toolkit 1.6//EN");
    xmlProperties.setProperty(OutputKeys.DOCTYPE_SYSTEM,
            "http://google-web-toolkit.googlecode.com/svn/releases/1.6/distro-source/core/src/gwt-module.dtd");
  }

  @Override
  public boolean install() {
    final File module = configFactory.getProjectConfig(project).getProjectProperty(ProjectProperty.MODULE_FILE, File.class);
    if (!module.exists()) {
      module.getParentFile().mkdirs();
      try {
        module.createNewFile();
      }
      catch (IOException e) {
        printError("Could not create module at " + module.getAbsolutePath(), e);
        return false;
      }

      final FileWriter writer;
      try {
        writer = new FileWriter(module);
        writer.append(emptyModuleContents);
      }
      catch (IOException e) {
        printError("Cannot write to module at " + module.getAbsolutePath(), e);
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

}
