package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault.Module;

public class ErraiJaxrsClientModuleFacet extends AbstractModuleFacet {

  public ErraiJaxrsClientModuleFacet() {
    modules = Arrays.asList(new Module[] {
        Module.ErraiJaxrs
    });
  }

}
