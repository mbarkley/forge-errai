package org.jboss.errai.forge.constant;


public final class ArtifactVault {
  
  public static final String ERRAI_GROUP_ID = "org.jboss.errai";
  
  public static enum DependencyArtifact {
    // Non-errai
    GwtUser("gwt-user", "com.google.gwt"),
    Guava("guava", "com.google.guava"),
    Hsq("hsqldb", "hsqldb"),
    JUnit("junit", "junit"),
    GwtSlf4j("gwt-slf4j", "de.benediktmeurer.gwt-slf4j"),
    
    // errai
    ErraiNetty("netty", "org.jboss.errai.io.netty"),
    ErraiJboss("errai-cdi-jboss"),
    ErraiCommon("errai-common"),
    ErraiTools("errai-tools"),
    ErraiBus("errai-bus");
    
    private final String artifactId;
    private final String groupId;
    
    private DependencyArtifact(final String artifactId, final String groupId) {
      this.artifactId = artifactId;
      this.groupId = groupId;
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
  }
  
  public static enum PluginArtifact {
    Clean("maven-clean-plugin", "org.apache.maven.plugins");
    
    private String artifactId;
    private String groupId;

    private PluginArtifact(final String artifactId, final String groupId) {
      this.artifactId = artifactId;
      this.groupId = groupId;
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
  }

}
