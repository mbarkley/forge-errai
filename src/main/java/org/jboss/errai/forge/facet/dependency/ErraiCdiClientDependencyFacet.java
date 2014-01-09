package org.jboss.errai.forge.facet.dependency;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * This facet sets the Maven dependencies needed to use the errai-cdi-client project.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ ErraiBusDependencyFacet.class, ErraiIocDependencyFacet.class })
public class ErraiCdiClientDependencyFacet extends AbstractDependencyFacet {

  public ErraiCdiClientDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(DependencyArtifact.ErraiCdiClient.toString()));
  }

}
