package org.jboss.errai.forge.facet.dependency;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.project.dependencies.DependencyBuilder;

public class ErraiJaxrsClientDependencyFacet extends AbstractDependencyFacet {
  
  public ErraiJaxrsClientDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(DependencyArtifact.ErraiJaxrsClient.toString()));
  }

}
