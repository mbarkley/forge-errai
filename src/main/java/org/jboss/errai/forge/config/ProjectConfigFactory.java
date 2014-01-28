package org.jboss.errai.forge.config;

import javax.enterprise.inject.Produces;

import org.jboss.forge.addon.projects.Project;

public class ProjectConfigFactory {
  
  private static ProjectConfig singleton;
  
  @Produces
  public ProjectConfig getProjectConfig(final Project project) {
    if (singleton == null)
      singleton = new ProjectConfig(project);
    return singleton;
  }

}
