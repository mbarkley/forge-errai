package org.jboss.errai.forge.constant;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.forge.project.dependencies.Dependency;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
public final class ArtifactVault {

  public static final String ERRAI_GROUP_ID = "org.jboss.errai";

  /**
   * An enumeration of Maven dependency artifacts used by various facets.
   * 
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static enum DependencyArtifact {
    // Non-errai
    GwtUser("gwt-user", "com.google.gwt"), Guava("guava", "com.google.guava"), Hsq("hsqldb", "hsqldb"), JUnit("junit",
            "junit"), GwtSlf4j("gwt-slf4j", "de.benediktmeurer.gwt-slf4j"), JavaxInject("javax.inject", "javax.inject"), CdiApi(
            "cdi-api", "javax.enterprise"),

    // plugins
    Clean("maven-clean-plugin", "org.apache.maven.plugins"), Dependency("maven-dependency-plugin",
            "org.apache.maven.plugins"), Compiler("maven-compiler-plugin", "org.apache.maven.plugins"), GwtPlugin(
            "gwt-maven-plugin", "org.codehaus.mojo"), War("maven-war-plugin", "org.apache.maven.plugins"), JbossPlugin(
            "jboss-as-maven-plugin", "org.jboss.as.plugins"),

    // errai
    ErraiNetty("netty", "org.jboss.errai.io.netty"), ErraiJboss("errai-cdi-jboss"), JbossSupport(
            "errai-jboss-as-support"), ErraiCommon("errai-common"), ErraiTools("errai-tools"), ErraiBus("errai-bus"), ErraiCdiClient(
            "errai-cdi-client"), ErraiWeldIntegration("errai-weld-integration"), ErraiCdiJetty("errai-cdi-jetty"), ErraiCodegenGwt(
            "errai-codegen-gwt"), ErraiIoc("errai-ioc");

    private final String artifactId;
    private final String groupId;

    private DependencyArtifact(final String artifactId, final String groupId) {
      this.artifactId = artifactId;
      this.groupId = groupId;
    }

    private DependencyArtifact(final String id) {
      this(id, ERRAI_GROUP_ID);
    }

    /**
     * @return The artifact id of this dependency.
     */
    public String getArtifactId() {
      return artifactId;
    }

    /**
     * @return The group id of this dependency.
     */
    public String getGroupId() {
      return groupId;
    }

    /**
     * Returns the string {@code groupId} + ":" + {@code artifactId}.
     */
    public String toString() {
      return String.format("%s:%s", groupId, artifactId);
    }

    private static Map<String, DependencyArtifact> artifacts = new HashMap<String, ArtifactVault.DependencyArtifact>();

    static {
      for (final DependencyArtifact artifact : DependencyArtifact.values()) {
        artifacts.put(artifact.getGroupId() + ":" + artifact.getArtifactId(), artifact);
      }
    }

    /**
     * Lookup a {@link DependencyArtifact} by the unique combination of it's
     * group id and artifact id.
     */
    public static DependencyArtifact valueOf(String groupId, String artifactId) {
      return artifacts.get(groupId + ":" + artifactId);
    }
  }

  /**
   * Blacklist of Maven dependencies which cannot be deployed in various profiles.
   */
  private static final Map<String, Set<String>> blacklist = new HashMap<String, Set<String>>();

  {
    // Wildfly/Jboss blacklist
    blacklist.put(AbstractBaseFacet.MAIN_PROFILE, new HashSet<String>());
    final Set<String> mainProfileBlacklist = blacklist.get(AbstractBaseFacet.MAIN_PROFILE);
    mainProfileBlacklist.add(DependencyArtifact.ErraiTools.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiJboss.toString());
    mainProfileBlacklist.add(DependencyArtifact.Hsq.toString());
    mainProfileBlacklist.add(DependencyArtifact.JUnit.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiNetty.toString());
    mainProfileBlacklist.add(DependencyArtifact.GwtSlf4j.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiCodegenGwt.toString());
    mainProfileBlacklist.add(DependencyArtifact.JavaxInject.toString());
    mainProfileBlacklist.add(DependencyArtifact.CdiApi.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiCdiJetty.toString());
  }
  
  public static boolean isBlacklisted(final String identifier) {
    for (final String profile : blacklist.keySet()) {
      if (blacklist.get(profile).contains(identifier))
        return true;
    }
    
    return false;
  }
  
  public static boolean isBlacklisted(final Dependency dep) {
    return isBlacklisted(dep.getGroupId() + ":" + dep.getArtifactId());
  }
  
  public static String getBlacklistedProfile(final String identifier) {
    for (final String profile : blacklist.keySet()) {
      if (blacklist.get(profile).contains(identifier)) 
        return profile;
    }
    
    return null;
  }
  
  public static String getBlacklistedProfile(final Dependency dep) {
    return getBlacklistedProfile(dep.getGroupId() + ":" + dep.getArtifactId());
  }
  
  public static Collection<String> getBlacklistProfiles() {
    return blacklist.keySet();
  }
  
  public static Collection<String> getBlacklistedArtifacts(final String profileId) {
    return blacklist.get(profileId);
  }

}
