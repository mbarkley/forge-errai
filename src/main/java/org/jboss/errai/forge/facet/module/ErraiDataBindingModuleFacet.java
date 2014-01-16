package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault.Module;

public class ErraiDataBindingModuleFacet extends AbstractModuleFacet {

  public ErraiDataBindingModuleFacet() {
    modules = Arrays.asList(new Module[] {
        Module.ErraiDataBinding
    });
  }

}
