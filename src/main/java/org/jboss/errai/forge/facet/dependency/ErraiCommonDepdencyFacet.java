package org.jboss.errai.forge.facet.dependency;

import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiCommon;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.GwtSlf4j;

import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;

public class ErraiCommonDepdencyFacet extends AbstractDependencyFacet {

  public ErraiCommonDepdencyFacet() {
    setCoreDependencies(
            DependencyBuilder.create(ErraiCommon.toString())
    );
    setProfileDependencies(MAIN_PROFILE,
            DependencyBuilder.create(GwtSlf4j.toString()).setScopeType(ScopeType.PROVIDED)
    );
  }
  
}
