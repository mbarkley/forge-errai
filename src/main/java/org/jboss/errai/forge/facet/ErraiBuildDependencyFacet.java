package org.jboss.errai.forge.facet;

import static org.jboss.errai.forge.constant.ArtifactVault.ArtifactId.*;

import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;

public class ErraiBuildDependencyFacet extends AbstractDependencyFacet {

  public ErraiBuildDependencyFacet() {
    setCoreDependencies(
            DependencyBuilder.create(ErraiTools.toString()),
            DependencyBuilder.create(GwtUser.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(ErraiJboss.toString()),
            DependencyBuilder.create(JUnit.toString()).setScopeType(ScopeType.TEST)
    );
    setProfileDependencies(PRODUCTION_PROFILE, 
            DependencyBuilder.create(ErraiTools.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(ErraiJboss.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(Hsq.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(JUnit.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(ErraiNetty.toString()).setScopeType(ScopeType.PROVIDED)
            );
  }
  
}
