package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault.Module;

public class ErraiJpaDatasyncModuleFacet extends AbstractModuleFacet {

  public ErraiJpaDatasyncModuleFacet() {
    modules = Arrays.asList(new Module[] {
        Module.ErraiJpaDatasync
    });
  }

}
