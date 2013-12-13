package org.jboss.errai.forge.facet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.profiles.ProfileBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ DependencyFacet.class, MavenCoreFacet.class })
abstract class AbstractDependencyFacet extends BaseFacet {

  protected static final String PRODUCTION_PROFILE = "production";

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
    final String version = VersionOracle.get(getProject()).resolveVersion();

    // Add dev mode build dependencies
    for (DependencyBuilder dep : coreDependencies) {
      depFacet.addDirectDependency(dep.setVersion(version));
    }
    // Create profiles
    for (Entry<String, Collection<DependencyBuilder>> entry : profileDependencies.entrySet()) {
      createNewProfile(entry.getKey(), entry.getValue(), version);
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
    for (Profile profile : coreFacet.getPOM().getProfiles()) {
      if (profileDependencies.containsKey(profile.getId())) {
        coreFacet.getPOM().removeProfile(profile);
      }
    }

    return true;
  }

  @Override
  public boolean isInstalled() {
    return getProject().hasFacet(getClass());
  }

  private Profile getProfile(final String name, final List<Profile> profiles) {
    for (final Profile profile : profiles) {
      if (profile.getId().equals(name))
        return profile;
    }

    return null;
  }

  private static Dependency convert(org.jboss.forge.project.dependencies.Dependency forgeDep) {
    Dependency retVal = new Dependency();

    retVal.setArtifactId(forgeDep.getArtifactId());
    retVal.setGroupId(forgeDep.getGroupId());
    retVal.setVersion(forgeDep.getVersion());
    retVal.setScope(forgeDep.getScopeType());
    if (ScopeType.SYSTEM.equals(forgeDep.getScopeTypeEnum()))
      retVal.setSystemPath(forgeDep.getSystemPath());
    retVal.setClassifier(forgeDep.getClassifier());
    retVal.setType(forgeDep.getPackagingType());

    for (org.jboss.forge.project.dependencies.Dependency dep : forgeDep.getExcludedDependencies()) {
      Exclusion exclude = new Exclusion();
      exclude.setArtifactId(dep.getArtifactId());
      exclude.setGroupId(dep.getGroupId());
      retVal.addExclusion(exclude);
    }

    return retVal;
  }

  protected boolean createNewProfile(final String name, final Collection<DependencyBuilder> deps, final String version) {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();

    Profile profile = getProfile(name, pom.getProfiles());

    if (profile == null) {
      profile = ProfileBuilder.create().setId(name).getAsMavenProfile();
      pom.addProfile(profile);
    }

    for (DependencyBuilder dep : deps) {
      profile.addDependency(convert(dep));
    }

    coreFacet.setPOM(pom);

    return true;
  }

  protected void setCoreDependencies(final DependencyBuilder... deps) {
    coreDependencies = Arrays.asList(deps);
  }

  protected void setProfileDependencies(final String name, final DependencyBuilder... deps) {
    profileDependencies.put(name, Arrays.asList(deps));
  }
  
}
