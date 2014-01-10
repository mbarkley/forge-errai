package org.jboss.errai.forge.facet.dependency;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.project.dependencies.DependencyBuilder;

public class ErraiNavigationDependencyFacet extends AbstractDependencyFacet {
  
  public ErraiNavigationDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(DependencyArtifact.ErraiNavigation.toString()));
  }

}
