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

/**
 * A base class for all facets that install Maven dependencies. Concrete
 * subclasses must provide values for
 * {@link AbstractDependencyFacet#coreDependencies} and
 * {@link AbstractDependencyFacet#profileDependencies}.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
abstract class AbstractDependencyFacet extends AbstractBaseFacet {

  /**
   * Dependencies to be added to the build in the Maven pom file. Versions of
   * these dependencies will be ignored and are re-assigned from a
   * {@link VersionOracle}.
   */
  protected Collection<DependencyBuilder> coreDependencies;
  /**
   * Dependencies to be added to the build of Maven profiles with names matching
   * the keys of this map. Versions of these dependencies will be ignored and
   * are re-assigned from a {@link VersionOracle}. Profiles that do not already
   * exist will be created.
   */
  protected Map<String, Collection<DependencyBuilder>> profileDependencies = new HashMap<String, Collection<DependencyBuilder>>();

  @Inject
  protected Shell shell;

  @Override
  public boolean install() {
    // TODO error handling and reversion

    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);
    final VersionOracle oracle = new VersionOracle(depFacet);

    for (DependencyBuilder dep : coreDependencies) {
      depFacet.addDirectDependency(getDependencyWithVersion(dep, oracle));
    }

    for (Entry<String, Collection<DependencyBuilder>> entry : profileDependencies.entrySet()) {
      addDependenciesToProfile(entry.getKey(), entry.getValue(), oracle);
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

  /**
   * A convenience method for setting
   * {@link AbstractDependencyFacet#coreDependencies}.
   * 
   * @param deps
   *          Dependencies to be put in a {@link Collection} and assigned to
   *          {@link AbstractDependencyFacet#coreDependencies}.
   */
  protected void setCoreDependencies(final DependencyBuilder... deps) {
    coreDependencies = Arrays.asList(deps);
  }

  /**
   * A convenience method for setting a key-value pair in
   * {@link AbstractDependencyFacet#profileDependencies}.
   * 
   * @param name
   *          The name of a Maven profile. If no profile with this name exists,
   *          one will be added to the pom file.
   * @param deps
   *          Dependencies to be put in a {@link Collection} and added to
   *          {@link AbstractDependencyFacet#profileDependencies}.
   */
  protected void setProfileDependencies(final String name, final DependencyBuilder... deps) {
    profileDependencies.put(name, Arrays.asList(deps));
  }

}
