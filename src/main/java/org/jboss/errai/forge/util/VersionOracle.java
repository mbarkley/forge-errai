package org.jboss.errai.forge.util;

import org.jboss.errai.forge.constant.ArtifactVault;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.constant.ArtifactVault.PluginArtifact;
import org.jboss.forge.project.Project;

public class VersionOracle {
  
  private Project project;
  
  public VersionOracle(Project project) {
    this.project = project;
  }
  
  public void setProject(Project project) {
    this.project = project;
  }
  
  public String resolveVersion(DependencyArtifact dependency) {
    return resolveVersion(dependency.getGroupId(), dependency.getArtifactId());
  }
  
  public String resolveVersion(PluginArtifact plugin) {
    return resolveVersion(plugin.getGroupId(), plugin.getArtifactId());
  }
  
  public String resolveVersion(String groupId, String artifactId) {
    if (groupId.equals(ArtifactVault.ERRAI_GROUP_ID))
      return resolveErraiVersion();
    
    // TODO better version strategy
    return null;
  }
  
  public String resolveErraiVersion() {
    // TODO actual implementation
    return "3.0-SNAPSHOT";
  }

}
