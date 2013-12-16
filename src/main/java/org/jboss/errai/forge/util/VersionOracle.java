package org.jboss.errai.forge.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.errai.forge.constant.ArtifactVault;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.constant.ArtifactVault.PluginArtifact;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;

public class VersionOracle {

  private static class ArtifactPair {
    private String groupId;
    private String artifactId;
    private ArtifactPair(String groupId, String artifactId) {
      this.groupId = groupId;
      this.artifactId = artifactId;
    }
    @Override
    public int hashCode() {
      return groupId.hashCode() + artifactId.hashCode();
    }
  }
  
  private DependencyFacet depFacet;
  private static final Map<ArtifactPair, String> versionMap = new ConcurrentHashMap<VersionOracle.ArtifactPair, String>();

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
      if (maxVersion == null || versionDep.getVersion().compareTo(maxVersion) > 0 && !versionDep.isSnapshot()) {
        maxVersion = versionDep.getVersion();
      }
    }
    
    return maxVersion;
  }

  private String getStaticVersion(String groupId, String artifactId) {
    return versionMap.get(new ArtifactPair(groupId, artifactId));
  }

  public String resolveErraiVersion() {
    DependencyArtifact common = ArtifactVault.DependencyArtifact.ErraiCommon;
    return getHighestStableVersion(common.getGroupId(), common.getArtifactId());
  }

}
