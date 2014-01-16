package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault.Module;

public class ErraiNavigationModuleFacet extends AbstractModuleFacet {

  public ErraiNavigationModuleFacet() {
    modules = Arrays.asList(new Module[] {
        Module.ErraiNavigation
    });
  }

}
