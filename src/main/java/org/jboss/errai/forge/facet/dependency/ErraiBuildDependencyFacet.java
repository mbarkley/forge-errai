package org.jboss.errai.forge.facet.dependency;

import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiJboss;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiTools;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.GwtUser;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.JUnit;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.facet.base.CoreBuildFacet;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * This facet sets all the common Maven dependencies required to build or
 * run in development mode an application with Errai.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ CoreBuildFacet.class })
public class ErraiBuildDependencyFacet extends AbstractDependencyFacet {

  public ErraiBuildDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(ErraiTools.toString()), DependencyBuilder.create(GwtUser.toString())
            .setScopeType(ScopeType.PROVIDED), DependencyBuilder.create(ErraiJboss.toString()), DependencyBuilder
            .create(JUnit.toString()).setScopeType(ScopeType.TEST));
  }

  @Override
  public boolean install() {
    if (super.install()) {
      // Set main profile to be active by default
      final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
      final Model pom = coreFacet.getPOM();
      final Profile profile = getProfile(MAIN_PROFILE, pom.getProfiles());
      profile.getActivation().setActiveByDefault(true);
      coreFacet.setPOM(pom);

      return true;
    }
    else {
      return false;
    }
  }
}
