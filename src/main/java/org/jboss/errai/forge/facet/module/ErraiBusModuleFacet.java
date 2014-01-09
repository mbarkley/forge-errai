package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault.Module;

/**
 * This facet adds the errai-bus GWT module to a project.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ErraiBusModuleFacet extends AbstractModuleFacet {
  
  public ErraiBusModuleFacet() {
    modules = Arrays.asList(new Module[] {
            Module.ErraiBus
    });
  }

}
