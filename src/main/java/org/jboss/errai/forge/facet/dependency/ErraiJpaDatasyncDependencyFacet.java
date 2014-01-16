package org.jboss.errai.forge.facet.dependency;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.project.dependencies.DependencyBuilder;

public class ErraiJpaDatasyncDependencyFacet extends AbstractDependencyFacet {

  public ErraiJpaDatasyncDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(DependencyArtifact.ErraiJpaDatasync.toString()));
  }

}
