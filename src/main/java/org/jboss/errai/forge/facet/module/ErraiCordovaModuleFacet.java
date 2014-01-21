package org.jboss.errai.forge.facet.module;

import java.util.Arrays;

import org.jboss.errai.forge.constant.ModuleVault;

public class ErraiCordovaModuleFacet extends AbstractModuleFacet {
  
  public ErraiCordovaModuleFacet() {
    modules = Arrays.asList(new ModuleVault.Module[] {
       ModuleVault.Module.ErraiCordova     
    });
  }

}
