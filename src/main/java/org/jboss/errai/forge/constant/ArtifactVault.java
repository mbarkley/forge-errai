package org.jboss.errai.forge.constant;

import java.util.HashMap;
import java.util.Map;


public final class ArtifactVault {
  
  public static final String ERRAI_GROUP_ID = "org.jboss.errai";
  
  public static enum DependencyArtifact {
    // Non-errai
    GwtUser("gwt-user", "com.google.gwt"),
    Guava("guava", "com.google.guava"),
    Hsq("hsqldb", "hsqldb"),
    JUnit("junit", "junit"),
    GwtSlf4j("gwt-slf4j", "de.benediktmeurer.gwt-slf4j"),
    JavaxInject("javax.inject", "javax.inject"),
    CdiApi("cdi-api", "javax.enterprise"),
    JavaEE("jboss-javaee-6.0", "org.jboss.spec", false),
    WeldCore("weld-core", "org.jboss.weld", false),
    WeldServletCore("weld-servlet-core", "org.jboss.weld.servlet", false),
    
    // plugins
    Clean("maven-clean-plugin", "org.apache.maven.plugins"),
    Dependency("maven-dependency-plugin", "org.apache.maven.plugins"),
    Compiler("maven-compiler-plugin", "org.apache.maven.plugins"),
    GwtPlugin("gwt-maven-plugin", "org.codehaus.mojo"),
    War("maven-war-plugin", "org.apache.maven.plugins"),
    JbossPlugin("jboss-as-maven-plugin", "org.jboss.as.plugins"),
    
    // errai
    ErraiBom("errai-bom", "org.jboss.errai", false),
    ErraiParent("errai-parent", "org.jboss.errai", false),
    ErraiNetty("netty", "org.jboss.errai.io.netty"),
    ErraiJboss("errai-cdi-jboss", "org.jboss.errai", false),
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
    private final boolean managed;
    
    private DependencyArtifact(final String artifactId, final String groupId, boolean managed) {
      this.artifactId = artifactId;
      this.groupId = groupId;
      this.managed = managed;
    }
    
    private DependencyArtifact(final String artifactId, final String groupId) {
      this(artifactId, groupId, true);
    }

    private DependencyArtifact(final String id) {
      this(id, ERRAI_GROUP_ID);
    }
    
    public String getArtifactId() {
      return artifactId;
    }
    
    public String getGroupId() {
      return groupId;
    }
    
    @Override
    public String toString() {
      return String.format("%s:%s", groupId, artifactId);
    }

    public boolean isManaged() {
      return managed;
    }
    
    private static Map<String, DependencyArtifact> artifacts = new HashMap<String, ArtifactVault.DependencyArtifact>();
    
    static {
      for (final DependencyArtifact artifact : DependencyArtifact.values()) {
        artifacts.put(artifact.getGroupId() + ":" + artifact.getArtifactId(), artifact);
      }
    }
    
    public static DependencyArtifact valueOf(String groupId, String artifactId) {
      return artifacts.get(groupId + ":" + artifactId);
    }
  }
  
  public static boolean isManaged(final String groupId, final String artifactId) {
    final DependencyArtifact artifact = DependencyArtifact.valueOf(groupId, artifactId);
    return artifact != null && artifact.isManaged();
  }
  
}
