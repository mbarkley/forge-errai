package org.jboss.errai.forge.facet;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.constant.ArtifactVault.ArtifactId;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.profiles.ProfileBuilder;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class AbstractDependencyFacetTest extends AbstractShellTest {

  public static class NoProfileDependencyFacet extends AbstractDependencyFacet {
    public NoProfileDependencyFacet() {
      coreDependencies = Arrays.asList(new DependencyBuilder[] { DependencyBuilder.create(ArtifactId.ErraiCommon
              .toString()) });
      profileDependencies = new HashMap<String, Collection<DependencyBuilder>>();
    }
  }

  public static class ProfileDependencyFacet extends AbstractDependencyFacet {
    public ProfileDependencyFacet() {
      coreDependencies = Arrays.asList(new DependencyBuilder[0]);
      profileDependencies = new HashMap<String, Collection<DependencyBuilder>>();
      profileDependencies.put("myProfile",
              Arrays.asList(new DependencyBuilder[] { DependencyBuilder.create(ArtifactId.ErraiCommon.toString()) }));
    }
  }

  @Test
  public void testNoProfileEmptyInstall() throws Exception {
    final Project project = initializeJavaProject();
    NoProfileDependencyFacet facet = new NoProfileDependencyFacet();

    project.installFacet(facet);

    assertTrue(project.hasFacet(NoProfileDependencyFacet.class));
    assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(
            DependencyBuilder.create(ArtifactId.ErraiCommon.toString() + ":3.0-SNAPSHOT")));
  }

  @Test
  public void testProfileEmptyInstall() throws Exception {
    final Project project = initializeJavaProject();
    ProfileDependencyFacet facet = new ProfileDependencyFacet();

    project.installFacet(facet);

    assertTrue(project.hasFacet(ProfileDependencyFacet.class));
    List<Profile> profiles = project.getFacet(MavenCoreFacet.class).getPOM().getProfiles();
    assertEquals(1, profiles.size());
    assertEquals("myProfile", profiles.get(0).getId());
    assertEquals(1, profiles.get(0).getDependencies().size());
    assertEquals(ArtifactId.ErraiCommon.getArtifactId(), profiles.get(0).getDependencies().get(0).getArtifactId());
  }

  @Test
  public void testProfileExistingProfile() throws Exception {
    final Project project = initializeJavaProject();
    ProfileDependencyFacet facet = new ProfileDependencyFacet();
    MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    Model pom = coreFacet.getPOM();
    pom.addProfile(ProfileBuilder.create().setId("myProfile")
            .addDependency(DependencyBuilder.create("org.jboss.errai:errai-ui:3.0-SNAPSHOT")).getAsMavenProfile());
    coreFacet.setPOM(pom);

    project.installFacet(facet);

    assertTrue(project.hasFacet(facet.getClass()));
    List<Profile> profiles = coreFacet.getPOM().getProfiles();
    assertEquals(1, profiles.size());
    assertEquals("myProfile", profiles.get(0).getId());
    assertEquals(2, profiles.get(0).getDependencies().size());
    assertEquals("errai-ui", profiles.get(0).getDependencies().get(0).getArtifactId());
    assertEquals(ArtifactId.ErraiCommon.getArtifactId(), profiles.get(0).getDependencies().get(1).getArtifactId());
  }

}
