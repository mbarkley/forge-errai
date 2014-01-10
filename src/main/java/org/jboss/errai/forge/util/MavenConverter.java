package org.jboss.errai.forge.util;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.jboss.forge.project.dependencies.ScopeType;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class MavenConverter {

  /**
   * Convert a forge {@link org.jboss.forge.project.dependencies.Dependency} to
   * a Maven {@link Dependency}.
   */
  public static Dependency convert(org.jboss.forge.project.dependencies.Dependency forgeDep) {
    Dependency retVal = new Dependency();

    retVal.setArtifactId(forgeDep.getArtifactId());
    retVal.setGroupId(forgeDep.getGroupId());
    retVal.setVersion(forgeDep.getVersion());
    retVal.setScope(forgeDep.getScopeType());
    if (ScopeType.SYSTEM.equals(forgeDep.getScopeTypeEnum()))
      retVal.setSystemPath(forgeDep.getSystemPath());
    retVal.setClassifier(forgeDep.getClassifier());
    retVal.setType(forgeDep.getPackagingType());

    for (org.jboss.forge.project.dependencies.Dependency dep : forgeDep.getExcludedDependencies()) {
      Exclusion exclude = new Exclusion();
      exclude.setArtifactId(dep.getArtifactId());
      exclude.setGroupId(dep.getGroupId());
      retVal.addExclusion(exclude);
    }

    return retVal;
  }

  /**
   * @param mavenDep
   *          A native Maven model of a Maven dependency artifact.
   * @param forgeDep
   *          A forge model of a Maven dependency artifact.
   * @return True iff both models have the same group and artifact IDs.
   */
  public static boolean areSameArtifact(final Dependency mavenDep,
          final org.jboss.forge.project.dependencies.Dependency forgeDep) {
    return mavenDep.getGroupId().equals(forgeDep.getGroupId())
            && mavenDep.getArtifactId().equals(forgeDep.getArtifactId());
  }

  /**
   * @param dep1
   *          A forge model of a Maven dependency artifact.
   * @param dep2
   *          A forge model of a Maven dependency artifact.
   * @return True iff both models have the same group and artifact IDs.
   */
  public static boolean areSameArtifact(final Dependency dep1, final Dependency dep2) {
    return dep1.getGroupId().equals(dep2.getGroupId()) && dep1.getArtifactId().equals(dep2.getArtifactId());
  }

  /**
   * @param dep1
   *          A native Maven model of a Maven dependency artifact.
   * @param dep2
   *          A native Maven model of a Maven dependency artifact.
   * @return True iff both models have the same group and artifact IDs.
   */
  public static boolean areSameArtifact(final org.jboss.forge.project.dependencies.Dependency dep1,
          final org.jboss.forge.project.dependencies.Dependency dep2) {
    return dep1.getGroupId().equals(dep2.getGroupId()) && dep1.getArtifactId().equals(dep2.getArtifactId());
  }
}
