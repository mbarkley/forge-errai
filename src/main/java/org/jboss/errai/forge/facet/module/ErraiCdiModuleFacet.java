package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault.Module;

public class ErraiCdiModuleFacet extends AbstractModuleFacet {
  
  public ErraiCdiModuleFacet() {
    modules = Arrays.asList(new Module[] {
            Module.ErraiCdi
    });
  }

}
