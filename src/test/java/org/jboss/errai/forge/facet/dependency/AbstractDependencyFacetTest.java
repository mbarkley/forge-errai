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

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.profiles.ProfileBuilder;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class AbstractDependencyFacetTest extends AbstractShellTest {

  public static class NoProfileDependencyFacet extends AbstractDependencyFacet {
    public NoProfileDependencyFacet() {
      coreDependencies = Arrays.asList(new DependencyBuilder[] { DependencyBuilder
              .create(DependencyArtifact.ErraiCommon.toString()) });
      profileDependencies = new HashMap<String, Collection<DependencyBuilder>>();
    }
  }

  public static class ProfileDependencyFacet extends AbstractDependencyFacet {
    public ProfileDependencyFacet() {
      coreDependencies = Arrays.asList(new DependencyBuilder[0]);
      profileDependencies = new HashMap<String, Collection<DependencyBuilder>>();
      profileDependencies.put("myProfile", Arrays.asList(new DependencyBuilder[] { DependencyBuilder
              .create(DependencyArtifact.ErraiCommon.toString()) }));
    }
  }

  public static class BlacklistedDependencyFacet extends AbstractDependencyFacet {
    public BlacklistedDependencyFacet() {
      coreDependencies = Arrays.asList(new DependencyBuilder[] { DependencyBuilder.create(DependencyArtifact.ErraiTools
              .toString()) });
    }
  }

  @Test
  public void testNoProfileEmptyInstall() throws Exception {
    final Project project = initializeJavaProject();
    NoProfileDependencyFacet facet = new NoProfileDependencyFacet();

    prepareProjectPom(project);

    project.installFacet(facet);

    assertTrue(project.hasFacet(NoProfileDependencyFacet.class));
    assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(
            DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString())));
  }

  @Test
  public void testProfileEmptyInstall() throws Exception {
    final Project project = initializeJavaProject();
    ProfileDependencyFacet facet = new ProfileDependencyFacet();
    prepareProjectPom(project);

    project.installFacet(facet);

    assertTrue(project.hasFacet(ProfileDependencyFacet.class));
    List<Profile> profiles = project.getFacet(MavenCoreFacet.class).getPOM().getProfiles();
    assertEquals(1, profiles.size());
    assertEquals("myProfile", profiles.get(0).getId());
    assertEquals(1, profiles.get(0).getDependencies().size());
    assertEquals(DependencyArtifact.ErraiCommon.getArtifactId(), profiles.get(0).getDependencies().get(0)
            .getArtifactId());
  }

  @Test
  public void testProfileExistingProfile() throws Exception {
    final Project project = initializeJavaProject();
    ProfileDependencyFacet facet = new ProfileDependencyFacet();
    MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    prepareProjectPom(project);
    Model pom = coreFacet.getPOM();
    pom.addProfile(ProfileBuilder.create().setId("myProfile")
            .addDependency(DependencyBuilder.create("org.jboss.errai:errai-ui")).getAsMavenProfile());
    coreFacet.setPOM(pom);

    project.installFacet(facet);

    assertTrue(project.hasFacet(facet.getClass()));
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
    ProfileDependencyFacet facet = new ProfileDependencyFacet();
    prepareProjectPom(project);
    MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    Model pom = coreFacet.getPOM();
    pom.addProfile(ProfileBuilder.create().setId("myProfile")
            .addDependency(DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString())).getAsMavenProfile());
    coreFacet.setPOM(pom);

    project.installFacet(facet);

    assertTrue(project.hasFacet(facet.getClass()));
    List<Profile> profiles = coreFacet.getPOM().getProfiles();
    assertEquals(1, profiles.size());
    assertEquals("myProfile", profiles.get(0).getId());
    assertEquals(1, profiles.get(0).getDependencies().size());
    assertEquals(DependencyArtifact.ErraiCommon.getArtifactId(), profiles.get(0).getDependencies().get(0).getArtifactId());
  }

  @Test
  public void testConflictingDependency() throws Exception {
    final Project project = initializeJavaProject();
    final NoProfileDependencyFacet facet = new NoProfileDependencyFacet();
    prepareProjectPom(project);

    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    depFacet.addDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString() + ":2.4.2.Final"));

    project.installFacet(facet);

    assertTrue(project.hasFacet(facet.getClass()));
    assertTrue(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString())
            .setVersion(Property.ErraiVersion.invoke())));
    assertFalse(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString()
            + "2.4.2.Final")));
  }

  @Test
  public void testNoProfileUninstall() throws Exception {
    // Setup
    final Project project = initializeJavaProject();
    NoProfileDependencyFacet facet = new NoProfileDependencyFacet();
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);

    prepareProjectPom(project);

    project.installFacet(facet);

    assertTrue("Precondition failed.", project.hasFacet(NoProfileDependencyFacet.class));
    assertTrue(
            "Precondition failed.",
            project.getFacet(DependencyFacet.class).hasDirectDependency(
                    DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString())));

    // Actual test
    facet = new NoProfileDependencyFacet();
    facet.setProject(project);
    project.removeFacet(facet);
    assertFalse(project.hasFacet(NoProfileDependencyFacet.class));
    assertEquals(0, coreFacet.getPOM().getDependencies().size());
  }

  @Test
  public void testProfileUninstall() throws Exception {
    // Setup
    final Project project = initializeJavaProject();
    ProfileDependencyFacet facet = new ProfileDependencyFacet();
    prepareProjectPom(project);

    project.installFacet(facet);

    assertTrue(project.hasFacet(ProfileDependencyFacet.class));
    List<Profile> profiles = project.getFacet(MavenCoreFacet.class).getPOM().getProfiles();
    assertEquals("Precondition failed.", 1, profiles.size());
    assertEquals("Precondition failed.", "myProfile", profiles.get(0).getId());
    assertEquals("Precondition failed.", 1, profiles.get(0).getDependencies().size());
    assertEquals("Precondition failed.", DependencyArtifact.ErraiCommon.getArtifactId(), profiles.get(0)
            .getDependencies().get(0).getArtifactId());

    // Actual test
    facet = new ProfileDependencyFacet();
    facet.setProject(project);
    project.removeFacet(facet);
    profiles = project.getFacet(MavenCoreFacet.class).getPOM().getProfiles();
    assertEquals(0, profiles.get(0).getDependencies().size());
  }

  @Test
  public void testNoProfileIsInstalled() throws Exception {
    // Setup
    final Project project = initializeJavaProject();
    NoProfileDependencyFacet facet = new NoProfileDependencyFacet();

    prepareProjectPom(project);
    facet.setProject(project);

    assertFalse(facet.isInstalled());

    project.installFacet(facet);

    assertTrue("Precondition failed.", project.hasFacet(NoProfileDependencyFacet.class));
    assertTrue(
            "Precondition failed.",
            project.getFacet(DependencyFacet.class).hasDirectDependency(
                    DependencyBuilder.create(DependencyArtifact.ErraiCommon.toString())));

    // Actual test
    facet = new NoProfileDependencyFacet();
    facet.setProject(project);
    assertTrue(facet.isInstalled());
  }

  @Test
  public void testProfileIsInstalled() throws Exception {
    // Setup
    final Project project = initializeJavaProject();
    ProfileDependencyFacet facet = new ProfileDependencyFacet();
    facet.setProject(project);
    prepareProjectPom(project);

    assertFalse(facet.isInstalled());

    project.installFacet(facet);

    assertTrue(project.hasFacet(ProfileDependencyFacet.class));
    List<Profile> profiles = project.getFacet(MavenCoreFacet.class).getPOM().getProfiles();
    assertEquals("Precondition failed.", 1, profiles.size());
    assertEquals("Precondition failed.", "myProfile", profiles.get(0).getId());
    assertEquals("Precondition failed.", 1, profiles.get(0).getDependencies().size());
    assertEquals("Precondition failed.", DependencyArtifact.ErraiCommon.getArtifactId(), profiles.get(0)
            .getDependencies().get(0).getArtifactId());

    // Actual test
    facet = new ProfileDependencyFacet();
    facet.setProject(project);
    assertTrue(facet.isInstalled());
  }

  @Test
  public void testBlacklistedDependency() throws Exception {
    final Project project = initializeJavaProject();
    final BlacklistedDependencyFacet facet = new BlacklistedDependencyFacet();
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    prepareProjectPom(project);
    Model pom = coreFacet.getPOM();

    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);

    project.installFacet(facet);
    pom = coreFacet.getPOM();

    assertTrue(project.hasFacet(facet.getClass()));
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
    final BlacklistedDependencyFacet facet = new BlacklistedDependencyFacet();
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    prepareProjectPom(project);
    Model pom = coreFacet.getPOM();

    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);

    /*
     * This is what makes this test different than the last: we want to check
     * that the facet won't add a dependency that is already scheduled to add.
     */
    facet.setProfileDependencies(AbstractDependencyFacet.MAIN_PROFILE,
            DependencyBuilder.create(DependencyArtifact.Hsq.toString()).setScopeType(ScopeType.PROVIDED));

    project.installFacet(facet);
    pom = coreFacet.getPOM();

    assertTrue(project.hasFacet(facet.getClass()));
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
    final BlacklistedDependencyFacet facet = new BlacklistedDependencyFacet();
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    prepareProjectPom(project);
    Model pom = coreFacet.getPOM();

    /*
     * This is what makes this test different than the last: we want to check
     * that the facet won't add a provided scoped dependency if one has been
     * added already.
     */
    pom.addProfile(ProfileBuilder
            .create()
            .setId(AbstractDependencyFacet.MAIN_PROFILE)
            .addDependency(DependencyBuilder.create(DependencyArtifact.Hsq.toString()).setScopeType(ScopeType.PROVIDED))
            .getAsMavenProfile());
    coreFacet.setPOM(pom);

    project.installFacet(facet);
    pom = coreFacet.getPOM();

    assertTrue(project.hasFacet(facet.getClass()));
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
    final BlacklistedDependencyFacet facet = new BlacklistedDependencyFacet();
    // Use a different facet to install and uninstall because this modifies internal state
    final BlacklistedDependencyFacet installFacet = new BlacklistedDependencyFacet();
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    prepareProjectPom(project);
    Model pom = coreFacet.getPOM();

    project.installFacet(installFacet);
    pom = coreFacet.getPOM();

    /*
     * Preconditions
     */
    assertTrue(project.hasFacet(installFacet.getClass()));
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
    facet.setProject(project);
    facet.uninstall();
    pom = coreFacet.getPOM();
    
    assertFalse(depFacet.hasDirectDependency(DependencyBuilder.create(DependencyArtifact.ErraiTools.toString())
            .setVersion(Property.ErraiVersion.invoke())));
    assertEquals(0, pom.getProfiles().get(0).getDependencies().size());
  }
  
  @Test
  public void testBlacklistedDependencyIsInstalledNegative() throws Exception {
    final Project project = initializeJavaProject();
    final BlacklistedDependencyFacet installFacet = new BlacklistedDependencyFacet();
    final BlacklistedDependencyFacet testFacet = new BlacklistedDependencyFacet();
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    Model pom = coreFacet.getPOM();
    prepareProjectPom(project);

    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);

    /*
     * Setup
     */
    project.installFacet(installFacet);
    pom = coreFacet.getPOM();

    assertTrue(project.hasFacet(installFacet.getClass()));
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
    
    testFacet.setProject(project);
    assertFalse(testFacet.isInstalled());
  }
  
  private void prepareProjectPom(final Project project) {
    final MavenCoreFacet coreFacet = project.getFacet(MavenCoreFacet.class);
    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    final Model pom = coreFacet.getPOM();
    
    pom.addProperty(Property.ErraiVersion.getName(), "3.0-SNAPSHOT");
    coreFacet.setPOM(pom);
    
    depFacet.addDirectManagedDependency(DependencyBuilder.create("org.jboss.errai:errai-parent")
            .setVersion(Property.ErraiVersion.invoke()).setScopeType(ScopeType.IMPORT).setPackagingType("pom"));
    depFacet.addDirectManagedDependency(DependencyBuilder.create("org.jboss.errai.bom:errai-version-master")
            .setVersion(Property.ErraiVersion.invoke()).setScopeType(ScopeType.IMPORT).setPackagingType("pom"));
    depFacet.addDirectManagedDependency(DependencyBuilder.create(DependencyArtifact.ErraiJboss.toString())
            .setVersion(Property.ErraiVersion.invoke()));
  }
}
