package org.jboss.errai.forge.util;

import org.jboss.forge.project.Project;

public class VersionOracle {
  
  private Project project;

  private VersionOracle(Project project) {
    this.project = project;
  }
  
  public static VersionOracle get(Project project) {
    return new VersionOracle(project);
  }
  
  public String resolveVersion() {
    // TODO actual implementation
    return "3.0-SNAPSHOT";
  }

}
