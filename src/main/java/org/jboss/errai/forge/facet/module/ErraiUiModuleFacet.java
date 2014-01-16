package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault.Module;

public class ErraiUiModuleFacet extends AbstractModuleFacet {

  public ErraiUiModuleFacet() {
    modules = Arrays.asList(new Module[] {
        Module.ErraiUi
    });
  }

}
