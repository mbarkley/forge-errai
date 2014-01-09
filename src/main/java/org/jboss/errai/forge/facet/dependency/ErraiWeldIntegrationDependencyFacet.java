package org.jboss.errai.forge.facet.dependency;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * This facet sets the Maven dependencies needed to use the errai-weld-integration project.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ ErraiCdiClientDependencyFacet.class, ErraiBusDependencyFacet.class })
public class ErraiWeldIntegrationDependencyFacet extends AbstractDependencyFacet {
  
  public ErraiWeldIntegrationDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(DependencyArtifact.ErraiWeldIntegration.toString()));
    setProfileDependencies(MAIN_PROFILE, DependencyBuilder.create(DependencyArtifact.ErraiCdiJetty.toString()).setScopeType(ScopeType.PROVIDED));
  }

}
