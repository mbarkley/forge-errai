package org.jboss.errai.forge.facet.base;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ CoreBuildFacet.class })
public class DependencyManagementFacet extends AbstractBaseFacet {

  protected Collection<DependencyBuilder> dependencies = new ArrayList<DependencyBuilder>();

  public DependencyManagementFacet() {
    dependencies.add(DependencyBuilder.create(DependencyArtifact.ErraiBom.toString())
            .setVersion(Property.ErraiVersion.invoke()).setScopeType(ScopeType.IMPORT).setPackagingType("pom"));
    dependencies.add(DependencyBuilder.create(DependencyArtifact.ErraiVersionMaster.toString())
            .setVersion(Property.ErraiVersion.invoke()).setScopeType(ScopeType.IMPORT).setPackagingType("pom"));
    dependencies.add(DependencyBuilder.create(DependencyArtifact.ErraiParent.toString())
            .setVersion(Property.ErraiVersion.invoke()).setScopeType(ScopeType.IMPORT).setPackagingType("pom"));
  }

  @Override
  public boolean install() {
    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);
    final VersionOracle oracle = new VersionOracle(depFacet);

    for (final DependencyBuilder dep : dependencies) {
      if (dep.getVersion() == null || dep.getVersion().equals("")) {
        dep.setVersion(oracle.resolveVersion(dep.getGroupId(), dep.getArtifactId()));
      }
      if (!depFacet.hasDirectManagedDependency(dep)) {
        depFacet.addDirectManagedDependency(dep);
      }
    }

    return true;
  }

  @Override
  public boolean uninstall() {
    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);

    for (final DependencyBuilder dep : dependencies) {
      if (depFacet.hasDirectManagedDependency(dep)) {
        depFacet.removeManagedDependency(dep);
      }
    }

    return true;
  }

  @Override
  public boolean isInstalled() {
    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);

    for (final DependencyBuilder dep : dependencies) {
      if (!depFacet.hasDirectManagedDependency(dep)) {
        return false;
      }
    }
    return true;
  }

}
