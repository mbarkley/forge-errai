package org.jboss.errai.forge.facet.dependency;

import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiCommon;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.GwtSlf4j;

import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;

/**
 * This facet sets the Maven dependencies needed to use the errai-common project.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
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
