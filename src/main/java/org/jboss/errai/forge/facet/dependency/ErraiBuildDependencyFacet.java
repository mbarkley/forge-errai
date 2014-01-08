package org.jboss.errai.forge.facet.dependency;

import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiJboss;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiNetty;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiTools;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.GwtUser;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.Hsq;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.JUnit;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.JbossSupport;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.facet.base.RequiresCore;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;

@RequiresCore
public class ErraiBuildDependencyFacet extends AbstractDependencyFacet {

  public ErraiBuildDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(ErraiTools.toString()), DependencyBuilder.create(GwtUser.toString())
            .setScopeType(ScopeType.PROVIDED), DependencyBuilder.create(ErraiJboss.toString()), DependencyBuilder
            .create(JUnit.toString()).setScopeType(ScopeType.TEST));
    setProfileDependencies(MAIN_PROFILE,
            DependencyBuilder.create(ErraiTools.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(ErraiJboss.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(JbossSupport.toString()),
            DependencyBuilder.create(Hsq.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(JUnit.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(ErraiNetty.toString()).setScopeType(ScopeType.PROVIDED)
    );
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
