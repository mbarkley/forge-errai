package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault.Module;

public class ErraiIocModulFacet extends AbstractModuleFacet {
  
  public ErraiIocModulFacet() {
    modules = Arrays.asList(new Module[] {
            Module.ErraiIoc
    });
  }

}
