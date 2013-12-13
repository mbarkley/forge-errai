package org.jboss.errai.forge.facet.dependency;

import static org.jboss.errai.forge.constant.ArtifactVault.ArtifactId.*;

import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ErraiBuildDependencyFacet.class})
public class ErraiCommonDepdencyFacet extends AbstractDependencyFacet {

  public ErraiCommonDepdencyFacet() {
    setCoreDependencies(
            DependencyBuilder.create(ErraiCommon.toString())
    );
    setProfileDependencies(PRODUCTION_PROFILE,
            DependencyBuilder.create(GwtSlf4j.toString()).setScopeType(ScopeType.PROVIDED)
    );
  }
  
}
