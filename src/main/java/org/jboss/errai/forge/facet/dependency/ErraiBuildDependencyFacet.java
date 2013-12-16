package org.jboss.errai.forge.facet.dependency;

import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.*;

import org.jboss.errai.forge.facet.plugin.CleanPluginFacet;
import org.jboss.errai.forge.facet.plugin.CompilerPluginFacet;
import org.jboss.errai.forge.facet.plugin.DependencyPluginFacet;
import org.jboss.errai.forge.facet.plugin.GwtPluginFacet;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({CleanPluginFacet.class, CompilerPluginFacet.class, DependencyPluginFacet.class, GwtPluginFacet.class})
public class ErraiBuildDependencyFacet extends AbstractDependencyFacet {

  public ErraiBuildDependencyFacet() {
    setCoreDependencies(
            DependencyBuilder.create(ErraiTools.toString()),
            DependencyBuilder.create(GwtUser.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(ErraiJboss.toString()),
            DependencyBuilder.create(JUnit.toString()).setScopeType(ScopeType.TEST)
    );
    setProfileDependencies(PRODUCTION_PROFILE, 
            DependencyBuilder.create(ErraiTools.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(ErraiJboss.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(Hsq.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(JUnit.toString()).setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create(ErraiNetty.toString()).setScopeType(ScopeType.PROVIDED)
            );
  }
  
}
