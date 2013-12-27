package org.jboss.errai.forge.facet.dependency;

import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;

import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.*;

public class ErraiIocDependencyFacet extends AbstractDependencyFacet {
  
  public ErraiIocDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(ErraiIoc.toString()));
    setProfileDependencies(MAIN_PROFILE,
            DependencyBuilder.create(ErraiCodegenGwt.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(JavaxInject.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(CdiApi.toString()).setScopeType(ScopeType.PROVIDED)
            );
  }

}