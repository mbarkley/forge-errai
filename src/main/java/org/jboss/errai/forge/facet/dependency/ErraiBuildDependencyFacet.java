package org.jboss.errai.forge.facet.dependency;

import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiJboss;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiNetty;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.ErraiTools;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.GwtUser;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.Hsq;
import static org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact.JUnit;

import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.jboss.errai.forge.facet.plugin.CleanPluginFacet;
import org.jboss.errai.forge.facet.plugin.CompilerPluginFacet;
import org.jboss.errai.forge.facet.plugin.DependencyPluginFacet;
import org.jboss.errai.forge.facet.plugin.GwtPluginFacet;
import org.jboss.errai.forge.facet.plugin.JbossPluginFacet;
import org.jboss.errai.forge.facet.plugin.WarPluginFacet;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ CleanPluginFacet.class, CompilerPluginFacet.class, DependencyPluginFacet.class, GwtPluginFacet.class,
    JbossPluginFacet.class, WarPluginFacet.class })
public class ErraiBuildDependencyFacet extends AbstractDependencyFacet {

  public final String javaSrcPath = "src/main/java";
  public final String resSrcPath = "src/main/resources";
  public final String buildOutput = "src/main/webapp/WEB-INF/classes";

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
  
  @Override
  public boolean install() {
    if (super.install()) {
      final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
      final Model pom = coreFacet.getPOM();
      final Build build = pom.getBuild();
      
      build.setOutputDirectory(buildOutput);
      
      Resource res = getResource(javaSrcPath, build.getResources());
      if (res == null) {
        res = new Resource();
        res.setDirectory(javaSrcPath);
        build.addResource(res);
      }
      res = getResource(resSrcPath, build.getResources());
      if (res == null) {
        res = new Resource();
        res.setDirectory(resSrcPath);
        build.addResource(res);
      }
      coreFacet.setPOM(pom);
      
      return true;
    }
    else {
      return false;
    }
  }

  private Resource getResource(String relPath, List<Resource> resources) {
    for (final Resource res : resources) {
      if (res.getDirectory().endsWith(relPath))
        return res;
    }
    
    return null;
  }

}
