package org.jboss.errai.forge.facet.dependency;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.project.dependencies.DependencyBuilder;

public class ErraiDataBindingDependencyFacet extends AbstractDependencyFacet {

  public ErraiDataBindingDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(DependencyArtifact.ErraiDataBinding.toString()));
  }

}
