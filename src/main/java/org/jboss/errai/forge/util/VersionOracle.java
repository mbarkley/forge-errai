package org.jboss.errai.forge.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.project.artifact.PluginArtifact;
import org.jboss.errai.forge.constant.ArtifactVault;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;

public class VersionOracle {

  private DependencyFacet depFacet;
  private static final Map<String, String> versionMap = new ConcurrentHashMap<String, String>();

  static {
    // Plugin versions
    versionMap.put(DependencyArtifact.Clean.toString(), "2.4.1");
    versionMap.put(DependencyArtifact.Compiler.toString(), "2.3.2");
    versionMap.put(DependencyArtifact.Dependency.toString(), "2.8");
    versionMap.put(DependencyArtifact.Gwt.toString(), "2.5.1");
  }
  
  public VersionOracle(DependencyFacet facet) {
    depFacet = facet;
  }

  public String resolveVersion(DependencyArtifact dependency) {
    return resolveVersion(dependency.getGroupId(), dependency.getArtifactId());
  }

  public String resolveVersion(PluginArtifact plugin) {
    return resolveVersion(plugin.getGroupId(), plugin.getArtifactId());
  }

  public String resolveVersion(String groupId, String artifactId) {
    String staticVersion = getStaticVersion(groupId, artifactId);
    if (staticVersion != null)
      return staticVersion;
    else if (groupId.equals(ArtifactVault.ERRAI_GROUP_ID))
      return resolveErraiVersion();
    else
      return getHighestStableVersion(groupId, artifactId);
  }

  private String getHighestStableVersion(String groupId, String artifactId) {
    final Dependency dep = DependencyBuilder.create(groupId + ":" + artifactId);
    final List<Dependency> availVersions = depFacet.resolveAvailableVersions(dep);
    
    String maxVersion = null;
    for (final Dependency versionDep : availVersions) {
      if (!versionDep.isSnapshot() && (maxVersion == null || versionDep.getVersion().compareTo(maxVersion) > 0)) {
        maxVersion = versionDep.getVersion();
      }
    }
    
    return maxVersion;
  }

  private String getStaticVersion(String groupId, String artifactId) {
    return versionMap.get(groupId + ":" + artifactId);
  }

  public String resolveErraiVersion() {
    DependencyArtifact common = DependencyArtifact.ErraiCommon;
    return getHighestStableVersion(common.getGroupId(), common.getArtifactId());
  }

}
