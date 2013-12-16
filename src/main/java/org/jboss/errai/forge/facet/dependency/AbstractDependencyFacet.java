package org.jboss.errai.forge.facet.dependency;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.maven.model.Profile;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ DependencyFacet.class, MavenCoreFacet.class })
abstract class AbstractDependencyFacet extends AbstractBaseFacet {

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
    final VersionOracle oracle = new VersionOracle(depFacet);

    // Add dev mode build dependencies
    for (DependencyBuilder dep : coreDependencies) {
      depFacet.addDirectDependency(dep.setVersion(oracle.resolveVersion(dep.getGroupId(), dep.getArtifactId())));
    }
    // Create profiles
    for (Entry<String, Collection<DependencyBuilder>> entry : profileDependencies.entrySet()) {
      getOrMakeProfile(entry.getKey(), entry.getValue(), oracle);
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

  protected void setCoreDependencies(final DependencyBuilder... deps) {
    coreDependencies = Arrays.asList(deps);
  }

  protected void setProfileDependencies(final String name, final DependencyBuilder... deps) {
    profileDependencies.put(name, Arrays.asList(deps));
  }

}
