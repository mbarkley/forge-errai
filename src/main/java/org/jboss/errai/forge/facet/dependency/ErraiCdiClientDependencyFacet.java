package org.jboss.errai.forge.facet.dependency;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ ErraiBusDependencyFacet.class, ErraiIocDependencyFacet.class })
public class ErraiCdiClientDependencyFacet extends AbstractDependencyFacet {

  public ErraiCdiClientDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(DependencyArtifact.ErraiCdiClient.toString()));
    setProfileDependencies(MAIN_PROFILE);
  }

}
