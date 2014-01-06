package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault.Module;

public class ErraiBusModuleFacet extends AbstractModuleFacet {
  
  public ErraiBusModuleFacet() {
    modules = Arrays.asList(new Module[] {
            Module.ErraiBus
    });
  }

}
