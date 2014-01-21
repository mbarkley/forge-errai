package org.jboss.errai.forge.facet.base;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.ProjectConfigFactory;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.forge.maven.MavenCoreFacet;

/**
 * This facet configures the source folders, build output directory, and pom
 * properties for a project.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class CoreBuildFacet extends AbstractBaseFacet {

  public static final String DEV_CONTEXT = "${project.artifactId}";
  public static final String JBOSS_HOME = "${project.build.directory}/jboss-as-7.1.1.Final";
  public static final String JAVA_SRC_PATH = "src/main/java";
  public static final String RES_SRC_PATH = "src/main/resources";
  public static final String BUILD_OUTPUT = "src/main/webapp/WEB-INF/classes";

  @Inject
  private ProjectConfigFactory configFactory;

  private String getErraiVersion() {
    return configFactory.getProjectConfig(getProject()).getProjectProperty(ProjectProperty.ERRAI_VERSION, String.class);
  }

  @Override
  public boolean install() {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();
    Build build = pom.getBuild();
    if (build == null) {
      build = new Build();
      pom.setBuild(build);
    }

    build.setOutputDirectory(BUILD_OUTPUT);
    // TODO don't hardcode path
    pom.addProperty(Property.JbossHome.getName(), JBOSS_HOME);
    pom.addProperty(Property.DevContext.getName(), DEV_CONTEXT);
    pom.addProperty(Property.ErraiVersion.getName(), getErraiVersion());

    Resource res = getResource(JAVA_SRC_PATH, build.getResources());
    if (res == null) {
      res = new Resource();
      res.setDirectory(JAVA_SRC_PATH);
      build.addResource(res);
    }
    res = getResource(RES_SRC_PATH, build.getResources());
    if (res == null) {
      res = new Resource();
      res.setDirectory(RES_SRC_PATH);
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
    return !(build == null || build.getOutputDirectory() == null || !build.getOutputDirectory().equals(BUILD_OUTPUT)
            || !properties.containsKey(Property.JbossHome.getName())
            || !properties.get(Property.JbossHome.getName()).equals(JBOSS_HOME)
            || !properties.containsKey(Property.DevContext.getName())
            || !properties.get(Property.DevContext.getName()).equals(DEV_CONTEXT)
            || !properties.containsKey(Property.ErraiVersion.getName())
            || !properties.get(Property.ErraiVersion.getName()).equals(getErraiVersion())
            || getResource(JAVA_SRC_PATH, build.getResources()) == null
            || getResource(RES_SRC_PATH, build.getResources()) == null);
  }

  private Resource getResource(String relPath, List<Resource> resources) {
    for (final Resource res : resources) {
      if (res.getDirectory().endsWith(relPath))
        return res;
    }

    return null;
  }
}
