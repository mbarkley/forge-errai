package org.jboss.errai.forge.facet;

import static org.jboss.errai.forge.constant.ArtifactVault.ArtifactId.*;

import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ErraiBuildDependencyFacet.class})
public class ErraiBusDependencyFacet extends AbstractDependencyFacet {
  
  public ErraiBusDependencyFacet() {
    setCoreDependencies(
            DependencyBuilder.create(ErraiBus.toString())
    );
    setProfileDependencies(PRODUCTION_PROFILE,
            DependencyBuilder.create(Guava.toString()).setScopeType(ScopeType.PROVIDED)
    );
  }

}
