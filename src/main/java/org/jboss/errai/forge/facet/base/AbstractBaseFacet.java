package org.jboss.errai.forge.facet.base;

import java.util.Collection;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.constant.ArtifactVault;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.errai.forge.util.MavenConverter;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.profiles.ProfileBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({MavenCoreFacet.class})
public abstract class AbstractBaseFacet extends BaseFacet {

  protected static final String MAIN_PROFILE = "jboss7";
  
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

  protected boolean makeProfile(final String name, final Collection<DependencyBuilder> deps,
          final VersionOracle versionOracle) {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();

    Profile profile = getProfile(name, pom.getProfiles());

    if (profile == null) {
      profile = ProfileBuilder.create().setId(name).getAsMavenProfile();
      pom.addProfile(profile);
    }

    for (DependencyBuilder dep : deps) {
      if (dep.getVersion() == null || dep.getVersion().equals("")) {
        if (dep.getGroupId().equals(ArtifactVault.ERRAI_GROUP_ID))
          dep.setVersion(Property.ErraiVersion.invoke());
        else
          dep.setVersion(versionOracle.resolveVersion(dep.getGroupId(), dep.getArtifactId()));
      }
      profile.addDependency(MavenConverter.convert(dep));
    }

    coreFacet.setPOM(pom);

    return true;
  }



}
