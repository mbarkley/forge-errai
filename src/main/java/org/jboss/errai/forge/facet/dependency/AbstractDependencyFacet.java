package org.jboss.errai.forge.facet.dependency;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

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
      if (!ArtifactVault.isManaged(dep.getGroupId(), dep.getArtifactId())) {
        if (dep.getGroupId().equals(ArtifactVault.ERRAI_GROUP_ID))
          dep.setVersion(Property.ErraiVersion.invoke());
        else
          dep.setVersion(oracle.resolveVersion(dep.getGroupId(), dep.getArtifactId()));
      }
      depFacet.addDirectDependency(dep);
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
