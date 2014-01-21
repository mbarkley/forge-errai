package org.jboss.errai.forge.facet.dependency;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.project.dependencies.DependencyBuilder;

public class ErraiCordovaDependencyFacet extends AbstractDependencyFacet {
  public ErraiCordovaDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(DependencyArtifact.ErraiCordova.toString()));
  }
}
