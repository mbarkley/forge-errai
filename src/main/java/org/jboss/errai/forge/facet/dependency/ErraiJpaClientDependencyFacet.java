package org.jboss.errai.forge.facet.dependency;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.project.dependencies.DependencyBuilder;

public class ErraiJpaClientDependencyFacet extends AbstractDependencyFacet {
  
  public ErraiJpaClientDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(DependencyArtifact.ErraiJpaClient.toString()));
  }

}
