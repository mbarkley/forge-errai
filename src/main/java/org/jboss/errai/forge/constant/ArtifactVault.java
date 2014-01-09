package org.jboss.errai.forge.constant;

import java.util.HashMap;
import java.util.Map;

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
    GwtUser("gwt-user", "com.google.gwt"),
    Guava("guava", "com.google.guava"),
    Hsq("hsqldb", "hsqldb"),
    JUnit("junit", "junit"),
    GwtSlf4j("gwt-slf4j", "de.benediktmeurer.gwt-slf4j"),
    JavaxInject("javax.inject", "javax.inject"),
    CdiApi("cdi-api", "javax.enterprise"),
    
    // plugins
    Clean("maven-clean-plugin", "org.apache.maven.plugins"),
    Dependency("maven-dependency-plugin", "org.apache.maven.plugins"),
    Compiler("maven-compiler-plugin", "org.apache.maven.plugins"),
    GwtPlugin("gwt-maven-plugin", "org.codehaus.mojo"),
    War("maven-war-plugin", "org.apache.maven.plugins"),
    JbossPlugin("jboss-as-maven-plugin", "org.jboss.as.plugins"),
    
    // errai
    ErraiNetty("netty", "org.jboss.errai.io.netty"),
    ErraiJboss("errai-cdi-jboss"),
    JbossSupport("errai-jboss-as-support"),
    ErraiCommon("errai-common"),
    ErraiTools("errai-tools"),
    ErraiBus("errai-bus"),
    ErraiCdiClient("errai-cdi-client"),
    ErraiWeldIntegration("errai-weld-integration"),
    ErraiCdiJetty("errai-cdi-jetty"),
    ErraiCodegenGwt("errai-codegen-gwt"),
    ErraiIoc("errai-ioc");
    
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
     * Lookup a {@link DependencyArtifact} by the unique combination of it's group id and artifact id.
     */
    public static DependencyArtifact valueOf(String groupId, String artifactId) {
      return artifacts.get(groupId + ":" + artifactId);
    }
  }
  
}
