package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault.Module;

public class ErraiJpaClientModuleFacet extends AbstractModuleFacet {

  public ErraiJpaClientModuleFacet() {
    modules = Arrays.asList(new Module[] {
            Module.ErraiJpa
    });
  }
}
