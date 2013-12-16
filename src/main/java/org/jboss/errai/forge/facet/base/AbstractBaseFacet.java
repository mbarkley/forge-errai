package org.jboss.errai.forge.facet.base;

import java.util.Collection;
import java.util.List;

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

public abstract class AbstractBaseFacet extends BaseFacet {

  @Override
  public boolean isInstalled() {
    return getProject().hasFacet(getClass());
  }

  protected Profile getProfile(final String name, final List<Profile> profiles) {
    for (final Profile profile : profiles) {
      if (profile.getId().equals(name))
        return profile;
    }

    return null;
  }

  protected static Dependency convert(org.jboss.forge.project.dependencies.Dependency forgeDep) {
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

  protected boolean getOrMakeProfile(final String name, final Collection<DependencyBuilder> deps,
          final VersionOracle versionOracle) {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();

    Profile profile = getProfile(name, pom.getProfiles());

    if (profile == null) {
      profile = ProfileBuilder.create().setId(name).getAsMavenProfile();
      pom.addProfile(profile);
    }

    for (DependencyBuilder dep : deps) {
      if (dep.getVersion() == null || dep.getVersion().equals(""))
        dep.setVersion(versionOracle.resolveVersion(dep.getGroupId(), dep.getArtifactId()));
      profile.addDependency(convert(dep));
    }

    coreFacet.setPOM(pom);

    return true;
  }



}
