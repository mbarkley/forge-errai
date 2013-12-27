package org.jboss.errai.forge.facet.dependency;

import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiBus;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.Guava;

import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ ErraiCommonDepdencyFacet.class })
public class ErraiBusDependencyFacet extends AbstractDependencyFacet {

  public ErraiBusDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(ErraiBus.toString()));
    setProfileDependencies(MAIN_PROFILE, DependencyBuilder.create(Guava.toString()).setScopeType(ScopeType.PROVIDED));
  }

}
