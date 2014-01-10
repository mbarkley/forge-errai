package org.jboss.errai.forge.facet.dependency;

import java.util.ArrayList;
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
import org.jboss.errai.forge.util.MavenConverter;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
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
   * these dependencies will be assigned from a {@link VersionOracle} if
   * unspecified.
   */
  protected Collection<DependencyBuilder> coreDependencies;
  /**
   * Dependencies to be added to the build of Maven profiles with names matching
   * the keys of this map. Versions of these dependencies will be assigned from
   * a {@link VersionOracle} if unspecified. Profiles that do not already exist
   * will be created.
   */
  protected Map<String, Collection<DependencyBuilder>> profileDependencies = new HashMap<String, Collection<DependencyBuilder>>();

  @Inject
  protected Shell shell;

  @Override
  public boolean install() {
    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final VersionOracle oracle = new VersionOracle(depFacet);

    for (DependencyBuilder dep : coreDependencies) {
      depFacet.addDirectDependency(getDependencyWithVersion(dep, oracle));
    }

    for (Entry<String, Collection<DependencyBuilder>> entry : profileDependencies.entrySet()) {
      addDependenciesToProfile(entry.getKey(), entry.getValue(), oracle);
    }

    final Model pom = coreFacet.getPOM();
    final Map<String, Collection<DependencyBuilder>> tmpProfileDependencies = new HashMap<String, Collection<DependencyBuilder>>();
    for (String profileId : ArtifactVault.getBlacklistProfiles()) {
      final Profile profile = getProfile(profileId, pom.getProfiles());
      for (String artifact : ArtifactVault.getBlacklistedArtifacts(profileId)) {
        DependencyBuilder dep = DependencyBuilder.create(artifact);
        if (depFacet.hasEffectiveDependency(dep) && !hasPendingProvidedDependency(profileId, dep)
                && !hasProvidedDependency(profile, dep)) {
          org.jboss.forge.project.dependencies.Dependency existing = depFacet.getEffectiveDependency(dep);
          dep.setVersion(existing.getVersion()).setScopeType(ScopeType.PROVIDED);
          if (!tmpProfileDependencies.containsKey(profileId))
            tmpProfileDependencies.put(profileId, new ArrayList<DependencyBuilder>());
          tmpProfileDependencies.get(profileId).add(dep);
        }
      }
    }
    
    for (Entry<String, Collection<DependencyBuilder>> entry : tmpProfileDependencies.entrySet()) {
      addDependenciesToProfile(entry.getKey(), entry.getValue(), oracle);
    }

    return true;
  }

  /**
   * Returns true iff there is a pending provided scoped dependency under the
   * given profile id.
   */
  private boolean hasPendingProvidedDependency(final String profileId, final DependencyBuilder dep) {
    if (profileDependencies.containsKey(profileId)) {
      for (final DependencyBuilder pendingDep : profileDependencies.get(profileId)) {
        if (MavenConverter.areSameArtifact(pendingDep, dep) && pendingDep.getScopeTypeEnum().equals(ScopeType.PROVIDED))
          return true;
      }
    }

    return false;
  }

  @Override
  public boolean uninstall() {
    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);
    for (DependencyBuilder dep : coreDependencies) {
      if (depFacet.hasDirectDependency(dep)) {
        depFacet.removeDependency(dep);
      }
    }

    // Remove blacklisted dependencies that are no longer transitively in the
    // project
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    Model pom = coreFacet.getPOM();
    for (final Profile profile : pom.getProfiles()) {
      for (final String artifactClassifier : ArtifactVault.getBlacklistedArtifacts(profile.getId())) {
        final DependencyBuilder dep = DependencyBuilder.create(artifactClassifier);
        if (!depFacet.hasEffectiveDependency(dep)) {
          if (!profileDependencies.containsKey(profile.getId()))
            profileDependencies.put(profile.getId(), new ArrayList<DependencyBuilder>());
          profileDependencies.get(profile.getId()).add(dep.setScopeType(ScopeType.PROVIDED));
        }
      }
    }

    pom = coreFacet.getPOM();
    for (Profile profile : pom.getProfiles()) {
      if (profileDependencies.containsKey(profile.getId())) {
        for (DependencyBuilder dep : profileDependencies.get(profile.getId())) {
          List<Dependency> profDeps = profile.getDependencies();
          for (int i = 0; i < profDeps.size(); i++) {
            if (MavenConverter.areSameArtifact(profDeps.get(i), dep)) {
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
    Model pom = coreFacet.getPOM();
    for (final String profName : profileDependencies.keySet()) {
      final Profile profile = getProfile(profName, pom.getProfiles());
      if (profile == null) {
        return false;
      }
      outer: for (final DependencyBuilder dep : profileDependencies.get(profName)) {
        for (final Dependency profDep : profile.getDependencies()) {
          if (MavenConverter.areSameArtifact(profDep, dep)) {
            continue outer;
          }
        }
        return false;
      }
    }

    /*
     * Check that all blacklisted dependencies that are transitively in the
     * project have been provided scoped. Note that we are not checking if these
     * blacklisted dependencies were pulled in transitively through a dependency
     * in this instance. This should not be an issue, since we don't really care
     * what sets the blacklisted dependencies to provided, as long as something
     * does it.
     */
    pom = coreFacet.getPOM();
    for (final String profileId : ArtifactVault.getBlacklistProfiles()) {
      final Profile profile = getProfile(profileId, pom.getProfiles());
      for (final String artifactClassifier : ArtifactVault.getBlacklistedArtifacts(profileId)) {
        final DependencyBuilder dep = DependencyBuilder.create(artifactClassifier);
        if (depFacet.hasEffectiveDependency(dep) && !hasProvidedDependency(profile, dep))
          return false;
      }
    }

    return true;
  }

  private DependencyBuilder getDependencyWithVersion(final DependencyBuilder dep, final VersionOracle oracle) {
    if (!oracle.isManaged(dep)) {
      if (dep.getGroupId().equals(ArtifactVault.ERRAI_GROUP_ID))
        dep.setVersion(Property.ErraiVersion.invoke());
      else
        dep.setVersion(oracle.resolveVersion(dep.getGroupId(), dep.getArtifactId()));
    }

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
    final List<DependencyBuilder> list = new ArrayList<DependencyBuilder>(deps.length);
    for (int i = 0; i < deps.length; i++) {
      list.add(deps[i]);
    }
    profileDependencies.put(name, list);
  }

}
