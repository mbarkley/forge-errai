package org.jboss.errai.forge.facet.base;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.errai.forge.constant.ArtifactVault;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.errai.forge.util.MavenConverter;
import org.jboss.errai.forge.util.VersionOracle;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.profiles.ProfileBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;

/**
 * A base class for Errai-related facets providing some basic routines.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class AbstractBaseFacet extends BaseFacet {

  /**
   * The name of the primary profile used to configure an Errai project.
   */
  public static final String MAIN_PROFILE = "jboss7";

  @Inject
  protected Shell shell;

  /**
   * Get a Maven {@link Profile} by name from a {@link List}.
   * 
   * @param name
   *          The name of the {@link Profile} to search for.
   * @param profiles
   *          A {@link List} of {@link Profile} objects to search.
   * @return The {@link Profile} in the given {@link List} matching the provided
   *         name, or {@literal null} if none was found.
   */
  protected Profile getProfile(final String name, final List<Profile> profiles) {
    for (final Profile profile : profiles) {
      if (profile.getId().equals(name))
        return profile;
    }

    return null;
  }

  /**
   * Print an error message to the {@link Shell}.
   * 
   * @param message
   *          A message to print to the shell.
   * @param throwable
   *          A stack trace from this will be printed iff
   *          {@link Shell#isVerbose()} is {@literal true}.
   */
  protected void printError(final String message, final Throwable throwable) {
    shell.println(ShellColor.RED, message);
    if (shell.isVerbose() && throwable != null) {
      for (final StackTraceElement trace : throwable.getStackTrace()) {
        shell.println(trace.toString());
      }
    }
  }

  /**
   * Add dependencies to a Maven profile.
   * 
   * @param name
   *          The name of the Maven profile to which dependencies will be added.
   *          If no profile with this name exists, one will be created.
   * @param deps
   *          Dependencies to be added. Note that the versions of these
   *          dependencies will be ignored, and instead provided by the
   *          {@link VersionOracle}.
   * @param versionOracle
   *          Used to determine the version of dependencies.
   * @return True iff the dependencies were successfully added.
   */
  protected boolean addDependenciesToProfile(final String name, final Collection<DependencyBuilder> deps,
          final VersionOracle versionOracle) {
    final MavenCoreFacet coreFacet = getProject().getFacet(MavenCoreFacet.class);
    final Model pom = coreFacet.getPOM();

    Profile profile = getProfile(name, pom.getProfiles());

    if (profile == null) {
      profile = ProfileBuilder.create().setId(name).getAsMavenProfile();
      pom.addProfile(profile);
    }

    for (DependencyBuilder dep : deps) {
      if (!hasDependency(profile, dep)) {
        if (!versionOracle.isManaged(dep)) {
          if (dep.getVersion() == null || dep.getVersion().equals("")) {
            if (dep.getGroupId().equals(ArtifactVault.ERRAI_GROUP_ID))
              dep.setVersion(Property.ErraiVersion.invoke());
            else
              dep.setVersion(versionOracle.resolveVersion(dep.getGroupId(), dep.getArtifactId()));
          }
        }
        profile.addDependency(MavenConverter.convert(dep));
      }
    }

    coreFacet.setPOM(pom);

    return true;
  }

  /**
   * Returns true iff the given profile as the given dependency (with provided
   * scope).
   */
  protected boolean hasProvidedDependency(final Profile profile, final DependencyBuilder dep) {
    final Dependency profDep = getDependency(profile, dep);

    return profDep != null && profDep.getScope() != null && profDep.getScope().equalsIgnoreCase("provided");
  }

  /**
   * Returns true iff the given profile as the given dependency.
   */
  protected boolean hasDependency(final Profile profile, final DependencyBuilder dep) {
    final Dependency profDep = getDependency(profile, dep);

    return profDep != null;
  }

  /**
   * Get a dependency if it exists in the given profile, or null.
   */
  protected Dependency getDependency(final Profile profile, final DependencyBuilder dep) {
    if (profile == null)
      return null;

    for (final Dependency profDep : profile.getDependencies()) {
      if (MavenConverter.areSameArtifact(profDep, dep))
        return profDep;
    }

    return null;
  }
}
