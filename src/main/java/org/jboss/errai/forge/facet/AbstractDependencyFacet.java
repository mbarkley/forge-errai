package org.jboss.errai.forge.facet;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.maven.model.Profile;
import org.jboss.errai.forge.constant.ErraiArtifact;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.profiles.ProfileBuilder;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyFilter;
import org.jboss.forge.project.dependencies.DependencyQueryBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({DependencyFacet.class, MavenCoreFacet.class})
abstract class AbstractDependencyFacet extends BaseFacet {

  /**
   * Version-less.
   */
  protected Collection<DependencyBuilder> coreDependencies;
  protected Map<String, Collection<DependencyBuilder>> profileDependencies;

  @Inject
  protected Shell shell;

  @Override
  public boolean install() {
    // TODO error handling and reversion
    
    final DependencyFacet depFacet = getProject().getFacet(DependencyFacet.class);
    final String version = resolveVersion(depFacet);

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

  protected boolean createNewProfile(final String name, final Collection<DependencyBuilder> deps, final String version) {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    ProfileBuilder profile = ProfileBuilder.create().setId(name);
    for (DependencyBuilder dep : deps) {
      profile.addDependency(dep.setVersion(version));
    }

    coreFacet.getPOM().addProfile(profile.getAsMavenProfile());

    return coreFacet.getPOM().getProfiles().contains(profile.getAsMavenProfile());
  }

  protected String resolveVersion(DependencyFacet depFacet) {
    // First check if there is already an errai dependency
    Dependency testDep = DependencyBuilder.create().setGroupId(ErraiArtifact.GROUP_ID);
    DependencyQueryBuilder query = DependencyQueryBuilder.create(testDep);
    DependencyFilter filter = query.getDependencyFilter();
    TreeSet<String> versions = new TreeSet<String>();
    for (Dependency dep : depFacet.getDependencies()) {
      if (filter.accept(dep)) {
        versions.add(dep.getVersion());
      }
    }

    if (versions.isEmpty()) {
      // prompt user
      Dependency version = shell.promptChoiceTyped("Please select a version to install.",
              depFacet.resolveAvailableVersions(query));
      return version.getVersion();
    }
    else if (versions.size() == 1) {
      return versions.last();
    }
    else {
      // Go with highest versions
      // TODO log warning about multiple versions
      return versions.last();
    }
  }
}
