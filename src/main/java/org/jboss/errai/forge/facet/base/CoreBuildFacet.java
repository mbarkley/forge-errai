package org.jboss.errai.forge.facet.base;

import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ MavenCoreFacet.class, DependencyFacet.class })
public class CoreBuildFacet extends AbstractBaseFacet {

  public final String javaSrcPath = "src/main/java";
  public final String resSrcPath = "src/main/resources";
  public final String buildOutput = "src/main/webapp/WEB-INF/classes";

  @Override
  public boolean install() {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();
    final Build build = pom.getBuild();

    build.setOutputDirectory(buildOutput);
    // TODO don't hardcode path
    pom.addProperty(Property.JbossHome.getName(), "${project.build.directory}/jboss-as-7.1.1.Final");
    pom.addProperty(Property.DevContext.getName(), "${project.artifactId}");
    pom.addProperty(Property.ErraiVersion.getName(),
            new VersionOracle(getProject().getFacet(DependencyFacet.class)).resolveErraiVersion());

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

  @Override
  public boolean isInstalled() {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();
    final Build build = pom.getBuild();

    Properties properties = pom.getProperties();
    return !(build == null || build.getOutputDirectory() == null || !build.getOutputDirectory().equals(buildOutput)
            || !properties.containsKey(Property.JbossHome.getName())
            || !properties.containsKey(Property.DevContext.getName())
            || !properties.containsKey(Property.ErraiVersion.getName())
            || getResource(javaSrcPath, build.getResources()) == null
            || getResource(resSrcPath, build.getResources()) == null);
  }

  private Resource getResource(String relPath, List<Resource> resources) {
    for (final Resource res : resources) {
      if (res.getDirectory().endsWith(relPath))
        return res;
    }

    return null;
  }
}
