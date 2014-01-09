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

}
