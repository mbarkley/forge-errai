package org.jboss.errai.forge.facet.dependency;

import java.util.Arrays;
import java.util.Collection;

import org.jboss.errai.forge.constant.ArtifactVault;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;

public class VersionManagementFacet extends AbstractBaseFacet {

  Collection<DependencyBuilder> dependencies = Arrays.asList(new DependencyBuilder[] {
          DependencyBuilder.create(DependencyArtifact.ErraiBom.toString()).setScopeType(ScopeType.IMPORT),
          DependencyBuilder.create(DependencyArtifact.ErraiParent.toString()).setScopeType(ScopeType.IMPORT),
          DependencyBuilder.create(DependencyArtifact.WeldCore.toString()),
          DependencyBuilder.create(DependencyArtifact.WeldServletCore.toString()),
          DependencyBuilder.create(DependencyArtifact.JavaEE.toString()).setScopeType(ScopeType.IMPORT)
  });
  
  @Override
  public boolean install() {
    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);
    final VersionOracle oracle = new VersionOracle(depFacet);
    for (final DependencyBuilder dep : dependencies) {
      if (ArtifactVault.ERRAI_GROUP_ID.equals(dep.getGroupId())) {
        dep.setVersion(Property.ErraiVersion.invoke());
      }
      else {
        dep.setVersion(oracle.resolveVersion(dep.getGroupId(), dep.getArtifactId()));
      }
      depFacet.addManagedDependency(dep);
    }
    
    return true;
  }
}
