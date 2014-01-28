package org.jboss.errai.forge.facet.dependency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.errai.forge.test.base.ForgeTest;
import org.jboss.errai.forge.util.MavenConverter;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.Faceted;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.junit.Test;

public class AbstractDependencyFacetTest extends ForgeTest {

  @Dependent
  public static class NoProfileDependencyFacet extends AbstractDependencyFacet {
    public NoProfileDependencyFacet() {
      coreDependencies = Arrays.asList(new DependencyBuilder[] { DependencyBuilder
              .create(DependencyArtifact.ErraiCommon.toString()) });
      profileDependencies = new HashMap<String, Collection<DependencyBuilder>>();
    }
  }

  @Dependent
  public static class ProfileDependencyFacet extends AbstractDependencyFacet {
    public ProfileDependencyFacet() {
      coreDependencies = Arrays.asList(new DependencyBuilder[0]);
      profileDependencies = new HashMap<String, Collection<DependencyBuilder>>();
      profileDependencies.put("myProfile", Arrays.asList(new DependencyBuilder[] { DependencyBuilder
              .create(DependencyArtifact.ErraiCommon.toString()) }));
    }
  }

  @Dependent
  public static class BlacklistedDependencyFacet extends AbstractDependencyFacet {
    public BlacklistedDependencyFacet() {
      coreDependencies = Arrays.asList(new DependencyBuilder[] { DependencyBuilder.create(DependencyArtifact.ErraiTools
              .toString()) });
    }
  }
  
  @Test
  public void testNoProfileEmptyInstall() throws Exception {
    final Project project = initializeJavaProject();

    prepareProjectPom(project);

    facetFactory.install((Faceted<ProjectFacet>) project, NoProfileDependencyFacet.class);

    assertTrue(project.hasFacet(NoProfileDependencyFacet.class.asSubclass(ProjectFacet.class)));
    assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(
            DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString())));
  }

  @Test
  public void testProfileEmptyInstall() throws Exception {
    final Project project = initializeJavaProject();
    prepareProjectPom(project);

    facetFactory.install((Faceted<ProjectFacet>) project, ProfileDependencyFacet.class);

    assertTrue(project.hasFacet(ProfileDependencyFacet.class.asSubclass(ProjectFacet.class)));
    List<Profile> profiles = project.getFacet(MavenFacet.class).getPOM().getProfiles();
    assertEquals(1, profiles.size());
    assertEquals("myProfile", profiles.get(0).getId());
    assertEquals(1, profiles.get(0).getDependencies().size());
    assertEquals(DependencyArtifact.ErraiCommon.getArtifactId(), profiles.get(0).getDependencies().get(0)
            .getArtifactId());
  }

  @Test
  public void testProfileExistingProfile() throws Exception {
    final Project project = initializeJavaProject();
    MavenFacet coreFacet = project.getFacet(MavenFacet.class);
    prepareProjectPom(project);

    final Profile profile = new Profile();
    profile.setId("myProfile");
    profile.addDependency(MavenConverter.convert(DependencyBuilder.create("org.jboss.errai:errai-ui")));

    Model pom = coreFacet.getPOM();
    pom.addProfile(profile);
    coreFacet.setPOM(pom);

    facetFactory.install((Faceted<ProjectFacet>) project, ProfileDependencyFacet.class);

    assertTrue(project.hasFacet(ProfileDependencyFacet.class.asSubclass(ProjectFacet.class)));
    List<Profile> profiles = coreFacet.getPOM().getProfiles();
    assertEquals(1, profiles.size());
    assertEquals("myProfile", profiles.get(0).getId());
    assertEquals(2, profiles.get(0).getDependencies().size());
    assertEquals("errai-ui", profiles.get(0).getDependencies().get(0).getArtifactId());
    assertEquals(DependencyArtifact.ErraiCommon.getArtifactId(), profiles.get(0).getDependencies().get(1)
            .getArtifactId());
  }

  @Test
  public void testProfileInstallNoDuplication() throws Exception {
    final Project project = initializeJavaProject();
    prepareProjectPom(project);
    MavenFacet coreFacet = project.getFacet(MavenFacet.class);

    final Profile profile = new Profile();
    profile.setId("myProfile");
    profile.addDependency(MavenConverter.convert(DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString())));

    Model pom = coreFacet.getPOM();
    pom.addProfile(profile);
    coreFacet.setPOM(pom);

    facetFactory.install((Faceted<ProjectFacet>) project, ProfileDependencyFacet.class);

    assertTrue(project.hasFacet(ProfileDependencyFacet.class.asSubclass(ProjectFacet.class)));
    List<Profile> profiles = coreFacet.getPOM().getProfiles();
    assertEquals(1, profiles.size());
    assertEquals("myProfile", profiles.get(0).getId());
    assertEquals(1, profiles.get(0).getDependencies().size());
    assertEquals(DependencyArtifact.ErraiCommon.getArtifactId(), profiles.get(0).getDependencies().get(0)
            .getArtifactId());
  }

  @Test
  public void testConflictingDependency() throws Exception {
    final Project project = initializeJavaProject();
    prepareProjectPom(project);

    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    depFacet.addDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString() + ":2.4.2.Final"));

    facetFactory.install((Faceted<ProjectFacet>) project, NoProfileDependencyFacet.class);

    assertTrue(project.hasFacet(NoProfileDependencyFacet.class.asSubclass(ProjectFacet.class)));
    assertTrue(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString())
            .setVersion(Property.ErraiVersion.invoke())));
    assertFalse(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString()
            + "2.4.2.Final")));
  }

  @Test
  public void testNoProfileUninstall() throws Exception {
    // Setup
    final Project project = initializeJavaProject();
    final MavenFacet coreFacet = project.getFacet(MavenFacet.class);

    prepareProjectPom(project);

    facetFactory.install((Faceted<ProjectFacet>) project, NoProfileDependencyFacet.class);

    assertTrue("Precondition failed.", project.hasFacet(NoProfileDependencyFacet.class.asSubclass(ProjectFacet.class)));
    assertTrue(
            "Precondition failed.",
            project.getFacet(DependencyFacet.class).hasDirectDependency(
                    DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString())));

    // Actual test
    final ProjectFacet facet = project.getFacet(NoProfileDependencyFacet.class.asSubclass(ProjectFacet.class));
    assertTrue(facet.uninstall());
    // assertFalse(project.hasFacet(NoProfileDependencyFacet.class.asSubclass(ProjectFacet.class)));
    assertEquals(0, coreFacet.getPOM().getDependencies().size());
  }

  @Test
  public void testProfileUninstall() throws Exception {
    // Setup
    final Project project = initializeJavaProject();
    prepareProjectPom(project);

    facetFactory.install((Faceted<ProjectFacet>) project, ProfileDependencyFacet.class);

    assertTrue(project.hasFacet(ProfileDependencyFacet.class.asSubclass(ProjectFacet.class)));
    List<Profile> profiles = project.getFacet(MavenFacet.class).getPOM().getProfiles();
    assertEquals("Precondition failed.", 1, profiles.size());
    assertEquals("Precondition failed.", "myProfile", profiles.get(0).getId());
    assertEquals("Precondition failed.", 1, profiles.get(0).getDependencies().size());
    assertEquals("Precondition failed.", DependencyArtifact.ErraiCommon.getArtifactId(), profiles.get(0)
            .getDependencies().get(0).getArtifactId());

    // Actual test
    final ProjectFacet facet = project.getFacet(ProfileDependencyFacet.class.asSubclass(ProjectFacet.class));
    assertTrue(facet.uninstall());
    profiles = project.getFacet(MavenFacet.class).getPOM().getProfiles();
    assertEquals(0, profiles.get(0).getDependencies().size());
  }

  @Test
  public void testNoProfileIsInstalled() throws Exception {
    // Setup
    final Project project = initializeJavaProject();
    NoProfileDependencyFacet facet = facetFactory.create((Faceted<ProjectFacet>) project,
            NoProfileDependencyFacet.class);

    prepareProjectPom(project);

    assertFalse(facet.isInstalled());

    facetFactory.install((Faceted<ProjectFacet>) project, NoProfileDependencyFacet.class);

    assertTrue("Precondition failed.", project.hasFacet(NoProfileDependencyFacet.class.asSubclass(ProjectFacet.class)));
    assertTrue(
            "Precondition failed.",
            project.getFacet(DependencyFacet.class).hasDirectDependency(
                    DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString())));

    // Actual test
    facet = facetFactory.create((Faceted<ProjectFacet>) project, NoProfileDependencyFacet.class);
    assertTrue(facet.isInstalled());
  }

  @Test
  public void testProfileIsInstalled() throws Exception {
    // Setup
    final Project project = initializeJavaProject();
    ProfileDependencyFacet facet = facetFactory.create((Faceted<ProjectFacet>) project, ProfileDependencyFacet.class);
    prepareProjectPom(project);

    assertFalse(facet.isInstalled());

    facetFactory.install((Faceted<ProjectFacet>) project, ProfileDependencyFacet.class);

    assertTrue(project.hasFacet(ProfileDependencyFacet.class.asSubclass(ProjectFacet.class)));
    List<Profile> profiles = project.getFacet(MavenFacet.class).getPOM().getProfiles();
    assertEquals("Precondition failed.", 1, profiles.size());
    assertEquals("Precondition failed.", "myProfile", profiles.get(0).getId());
    assertEquals("Precondition failed.", 1, profiles.get(0).getDependencies().size());
    assertEquals("Precondition failed.", DependencyArtifact.ErraiCommon.getArtifactId(), profiles.get(0)
            .getDependencies().get(0).getArtifactId());

    // Actual test
    facet = facetFactory.create((Faceted<ProjectFacet>) project, ProfileDependencyFacet.class);
    assertTrue(facet.isInstalled());
  }

  @Test
  public void testBlacklistedDependency() throws Exception {
    final Project project = initializeJavaProject();
    final MavenFacet coreFacet = project.getFacet(MavenFacet.class);
    prepareProjectPom(project);
    Model pom = coreFacet.getPOM();

    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);

    facetFactory.install((Faceted<ProjectFacet>) project, BlacklistedDependencyFacet.class);
    pom = coreFacet.getPOM();

    assertTrue(project.hasFacet(BlacklistedDependencyFacet.class.asSubclass(ProjectFacet.class)));
    assertTrue(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiTools.toString())
            .setVersion(Property.ErraiVersion.invoke())));
    // This dependency should have been transitively included through
    // errai-tools
    assertTrue(depFacet.hasEffectiveDependency(DependencyBuilder.create(DependencyArtifact.Hsq.toString())));

    assertEquals(1, pom.getProfiles().size());
    assertEquals(2, pom.getProfiles().get(0).getDependencies().size());

    final Set<String> providedClassifiers = new HashSet<String>(2);
    for (final Dependency provided : pom.getProfiles().get(0).getDependencies())
      providedClassifiers.add(provided.getGroupId() + ":" + provided.getArtifactId());

    assertTrue(providedClassifiers.contains(DependencyArtifact.ErraiTools.toString()));
    assertTrue(providedClassifiers.contains(DependencyArtifact.Hsq.toString()));
  }

  /**
   * Check that the facet won't doubly add a provided scope dependency that is
   * already set to be added in a profile within the facet.
   */
  @Test
  public void testBlacklistedDependencyNonDuplication1() throws Exception {
    final Project project = initializeJavaProject();
    final BlacklistedDependencyFacet facet = facetFactory.create((Faceted<ProjectFacet>) project,
            BlacklistedDependencyFacet.class);
    final MavenFacet coreFacet = project.getFacet(MavenFacet.class);
    prepareProjectPom(project);
    Model pom = coreFacet.getPOM();

    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);

    /*
     * This is what makes this test different than the last: we want to check
     * that the facet won't add a dependency that is already scheduled to add.
     */
    facet.setProfileDependencies(AbstractDependencyFacet.MAIN_PROFILE,
            DependencyBuilder.create(DependencyArtifact.Hsq.toString()).setScopeType("provided"));

    facet.install();
    pom = coreFacet.getPOM();

    assertTrue(project.hasFacet(BlacklistedDependencyFacet.class.asSubclass(ProjectFacet.class)));
    assertTrue(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiTools.toString())
            .setVersion(Property.ErraiVersion.invoke())));
    // This dependency should have been transitively included through
    // errai-tools
    assertTrue(depFacet.hasEffectiveDependency(DependencyBuilder.create(DependencyArtifact.Hsq.toString())));

    assertEquals(1, pom.getProfiles().size());
    assertEquals(2, pom.getProfiles().get(0).getDependencies().size());

    final Set<String> providedClassifiers = new HashSet<String>(2);
    for (final Dependency provided : pom.getProfiles().get(0).getDependencies())
      providedClassifiers.add(provided.getGroupId() + ":" + provided.getArtifactId());

    assertTrue(providedClassifiers.contains(DependencyArtifact.ErraiTools.toString()));
    assertTrue(providedClassifiers.contains(DependencyArtifact.Hsq.toString()));
  }

  /**
   * Check that the facet won't double add a provided scope dependency if it is
   * already in the appropriate profile of the pom.
   */
  @Test
  public void testBlacklistedDependencyNonDuplication2() throws Exception {
    final Project project = initializeJavaProject();
    final MavenFacet coreFacet = project.getFacet(MavenFacet.class);
    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    prepareProjectPom(project);
    Model pom = coreFacet.getPOM();

    /*
     * This is what makes this test different than the last: we want to check
     * that the facet won't add a provided scoped dependency if one has been
     * added already.
     */
    final Profile profile = new Profile();
    profile.setId(AbstractDependencyFacet.MAIN_PROFILE);
    profile.addDependency(MavenConverter.convert(DependencyBuilder.create(DependencyArtifact.Hsq.toString())
            .setScopeType("provided")));

    pom.addProfile(profile);
    coreFacet.setPOM(pom);

    facetFactory.install((Faceted<ProjectFacet>) project, BlacklistedDependencyFacet.class);
    pom = coreFacet.getPOM();

    assertTrue(project.hasFacet(BlacklistedDependencyFacet.class.asSubclass(ProjectFacet.class)));
    assertTrue(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiTools.toString())
            .setVersion(Property.ErraiVersion.invoke())));
    // This dependency should have been transitively included through
    // errai-tools
    assertTrue(depFacet.hasEffectiveDependency(DependencyBuilder.create(DependencyArtifact.Hsq.toString())));

    assertEquals(1, pom.getProfiles().size());
    assertEquals(2, pom.getProfiles().get(0).getDependencies().size());

    final Set<String> providedClassifiers = new HashSet<String>(2);
    for (final Dependency provided : pom.getProfiles().get(0).getDependencies())
      providedClassifiers.add(provided.getGroupId() + ":" + provided.getArtifactId());

    assertTrue(providedClassifiers.contains(DependencyArtifact.ErraiTools.toString()));
    assertTrue(providedClassifiers.contains(DependencyArtifact.Hsq.toString()));
  }

  @Test
  public void testBlacklistedDependencyUninstall() throws Exception {
    final Project project = initializeJavaProject();
    // Use a different facet to install and uninstall because this modifies
    // internal state
    final MavenFacet coreFacet = project.getFacet(MavenFacet.class);
    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    prepareProjectPom(project);
    Model pom = coreFacet.getPOM();

    facetFactory.install((Faceted<ProjectFacet>) project,
            BlacklistedDependencyFacet.class);
    pom = coreFacet.getPOM();

    /*
     * Preconditions
     */
    assertTrue(project.hasFacet(BlacklistedDependencyFacet.class.asSubclass(ProjectFacet.class)));
    assertTrue(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiTools.toString())
            .setVersion(Property.ErraiVersion.invoke())));
    // This dependency should have been transitively included through
    // errai-tools
    assertTrue(depFacet.hasEffectiveDependency(DependencyBuilder.create(DependencyArtifact.Hsq.toString())));

    assertEquals(1, pom.getProfiles().size());
    assertEquals(2, pom.getProfiles().get(0).getDependencies().size());

    final Set<String> providedClassifiers = new HashSet<String>(2);
    for (final Dependency provided : pom.getProfiles().get(0).getDependencies())
      providedClassifiers.add(provided.getGroupId() + ":" + provided.getArtifactId());

    assertTrue(providedClassifiers.contains(DependencyArtifact.ErraiTools.toString()));
    assertTrue(providedClassifiers.contains(DependencyArtifact.Hsq.toString()));

    /*
     * Actual Test
     */
    final BlacklistedDependencyFacet facet = facetFactory.create((Faceted<ProjectFacet>) project,
            BlacklistedDependencyFacet.class);
    facet.uninstall();
    pom = coreFacet.getPOM();

    assertFalse(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiTools.toString())
            .setVersion(Property.ErraiVersion.invoke())));
    assertEquals(0, pom.getProfiles().get(0).getDependencies().size());
  }

  @Test
  public void testBlacklistedDependencyIsInstalledNegative() throws Exception {
    final Project project = initializeJavaProject();
    final BlacklistedDependencyFacet testFacet = facetFactory.create((Faceted<ProjectFacet>) project,
            BlacklistedDependencyFacet.class);
    final MavenFacet coreFacet = project.getFacet(MavenFacet.class);
    Model pom = coreFacet.getPOM();
    prepareProjectPom(project);

    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);

    /*
     * Setup
     */
    facetFactory.install((Faceted<ProjectFacet>) project, BlacklistedDependencyFacet.class);
    pom = coreFacet.getPOM();

    assertTrue(project.hasFacet(BlacklistedDependencyFacet.class.asSubclass(ProjectFacet.class)));
    assertTrue(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiTools.toString())
            .setVersion(Property.ErraiVersion.invoke())));
    // This dependency should have been transitively included through
    // errai-tools
    assertTrue(depFacet.hasEffectiveDependency(DependencyBuilder.create(DependencyArtifact.Hsq.toString())));

    assertEquals(1, pom.getProfiles().size());
    assertEquals(2, pom.getProfiles().get(0).getDependencies().size());

    final Set<String> providedClassifiers = new HashSet<String>(2);
    for (final Dependency provided : pom.getProfiles().get(0).getDependencies())
      providedClassifiers.add(provided.getGroupId() + ":" + provided.getArtifactId());

    assertTrue(providedClassifiers.contains(DependencyArtifact.ErraiTools.toString()));
    assertTrue(providedClassifiers.contains(DependencyArtifact.Hsq.toString()));

    /*
     * Actual test
     */
    for (final Dependency dep : pom.getProfiles().get(0).getDependencies()) {
      if (DependencyArtifact.Hsq.toString().equals(dep.getGroupId() + ":" + dep.getArtifactId())) {
        pom.getProfiles().get(0).removeDependency(dep);
        break;
      }
    }
    coreFacet.setPOM(pom);

    assertFalse(testFacet.isInstalled());
  }

  private void prepareProjectPom(final Project project) {
    final MavenFacet coreFacet = project.getFacet(MavenFacet.class);
    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    final Model pom = coreFacet.getPOM();

    pom.addProperty(Property.ErraiVersion.getName(), "3.0-SNAPSHOT");
    coreFacet.setPOM(pom);

    depFacet.addDirectManagedDependency(DependencyBuilder.create("org.jboss.errai:errai-parent")
            .setVersion(Property.ErraiVersion.invoke()).setScopeType("import").setPackaging("pom"));
    depFacet.addDirectManagedDependency(DependencyBuilder.create("org.jboss.errai.bom:errai-version-master")
            .setVersion(Property.ErraiVersion.invoke()).setScopeType("import").setPackaging("pom"));
    depFacet.addDirectManagedDependency(DependencyBuilder.create(DependencyArtifact.ErraiJboss.toString())
            .setVersion(Property.ErraiVersion.invoke()));
  }
}
