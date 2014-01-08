package org.jboss.errai.forge.facet.dependency;

import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiBus;

import java.util.Collection;
import java.util.HashMap;

import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ ErraiCommonDepdencyFacet.class })
public class ErraiBusDependencyFacet extends AbstractDependencyFacet {

  public ErraiBusDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(ErraiBus.toString()));
    profileDependencies = new HashMap<String, Collection<DependencyBuilder>>();
  }

}
