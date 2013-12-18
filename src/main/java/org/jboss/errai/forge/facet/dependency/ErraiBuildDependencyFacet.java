package org.jboss.errai.forge.facet.dependency;

import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiJboss;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiNetty;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiTools;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.GwtUser;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.Hsq;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.JUnit;

import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;

public class ErraiBuildDependencyFacet extends AbstractDependencyFacet {

  public ErraiBuildDependencyFacet() {
    setCoreDependencies(DependencyBuilder.create(ErraiTools.toString()), DependencyBuilder.create(GwtUser.toString())
            .setScopeType(ScopeType.PROVIDED), DependencyBuilder.create(ErraiJboss.toString()), DependencyBuilder
            .create(JUnit.toString()).setScopeType(ScopeType.TEST));
    setProfileDependencies(MAIN_PROFILE,
            DependencyBuilder.create(ErraiTools.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(ErraiJboss.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(Hsq.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(JUnit.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(ErraiNetty.toString()).setScopeType(ScopeType.PROVIDED));
  }
}
