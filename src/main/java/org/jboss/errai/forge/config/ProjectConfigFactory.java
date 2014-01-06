package org.jboss.errai.forge.config;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.forge.env.ConfigurationFactory;
import org.jboss.forge.project.Project;

public class ProjectConfigFactory {
  
  private ConfigurationFactory factory;
  
  private static ProjectConfig singleton;

  @Inject
  public ProjectConfigFactory(final ConfigurationFactory factory) {
    this.factory = factory;
  }
  
  @Produces
  public ProjectConfig getProjectConfig(final Project project) {
    if (singleton == null)
      singleton = new ProjectConfig(factory, project);
    return singleton;
  }

}
