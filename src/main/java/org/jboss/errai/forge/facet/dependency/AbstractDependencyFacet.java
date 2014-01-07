package org.jboss.errai.forge.facet.dependency;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.constant.ArtifactVault;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.Shell;

abstract class AbstractDependencyFacet extends AbstractBaseFacet {

  /**
   * Version-less.
   */
  protected Collection<DependencyBuilder> coreDependencies;
  protected Map<String, Collection<DependencyBuilder>> profileDependencies = new HashMap<String, Collection<DependencyBuilder>>();

  @Inject
  protected Shell shell;

  @Override
  public boolean install() {
    // TODO error handling and reversion

    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);
    final VersionOracle oracle = new VersionOracle(depFacet);

    // Add dev mode build dependencies
    for (DependencyBuilder dep : coreDependencies) {
      depFacet.addDirectDependency(getDependencyWithVersion(dep, oracle));
    }
    // Create profiles
    for (Entry<String, Collection<DependencyBuilder>> entry : profileDependencies.entrySet()) {
      makeProfile(entry.getKey(), entry.getValue(), oracle);
    }

    return true;
  }

  @Override
  public boolean uninstall() {
    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);
    for (DependencyBuilder dep : coreDependencies) {
      if (depFacet.hasDirectDependency(dep)) {
        depFacet.removeDependency(dep);
      }
    }
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    Model pom = coreFacet.getPOM();
    for (Profile profile : pom.getProfiles()) {
      if (profileDependencies.containsKey(profile.getId())) {
        for (DependencyBuilder dep : profileDependencies.get(profile.getId())) {
          List<Dependency> profDeps = profile.getDependencies();
          for (int i = 0; i < profDeps.size(); i++) {
            if (profDeps.get(i).getArtifactId().equals(dep.getArtifactId())
                    && profDeps.get(i).getGroupId().equals(dep.getGroupId())) {
              profDeps.remove(i);
              break;
            }
          }
        }
      }
    }
    coreFacet.setPOM(pom);

    return true;
  }

  @Override
  public boolean isInstalled() {
    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);
    final VersionOracle oracle = new VersionOracle(depFacet);
    for (final DependencyBuilder dep : coreDependencies) {
      if (!depFacet.hasDirectDependency(getDependencyWithVersion(dep, oracle))) {
        return false;
      }
    }

    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();
    for (final String profName : profileDependencies.keySet()) {
      final Profile profile = getProfile(profName, pom.getProfiles());
      if (profile == null) {
        return false;
      }
      outer: for (final DependencyBuilder dep : profileDependencies.get(profName)) {
        for (final Dependency profDep : profile.getDependencies()) {
          if (profDep.getArtifactId().equals(dep.getArtifactId()) && profDep.getGroupId().equals(dep.getGroupId())) {
            continue outer;
          }
        }
        return false;
      }
    }

    return true;
  }

  private DependencyBuilder getDependencyWithVersion(final DependencyBuilder dep, final VersionOracle oracle) {
    if (dep.getGroupId().equals(ArtifactVault.ERRAI_GROUP_ID))
      dep.setVersion(Property.ErraiVersion.invoke());
    else
      dep.setVersion(oracle.resolveVersion(dep.getGroupId(), dep.getArtifactId()));

    return dep;
  }

  protected void setCoreDependencies(final DependencyBuilder... deps) {
    coreDependencies = Arrays.asList(deps);
  }

  protected void setProfileDependencies(final String name, final DependencyBuilder... deps) {
    profileDependencies.put(name, Arrays.asList(deps));
  }

}
