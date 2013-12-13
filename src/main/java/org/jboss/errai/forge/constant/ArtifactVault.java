package org.jboss.errai.forge.constant;


public final class ArtifactVault {
  
  public static final String GROUP_ID = "org.jboss.errai";
  
  public static enum ArtifactId {
    // Non-errai
    GwtUser("gwt-user", "com.google.gwt"),
    Guava("guava", "com.google.guava"),
    Hsq("hsqldb", "hsqldb"),
    JUnit("junit", "junit"),
    
    // errai
    ErraiNetty("netty", "org.jboss.errai.io.netty"),
    ErraiJboss("errai-cdi-jboss"),
    ErraiCommon("errai-common"),
    ErraiTools("errai-tools"),
    ErraiBus("errai-bus");
    
    private final String artifactId;
    private final String groupId;
    
    private ArtifactId(final String artifactId, final String groupId) {
      this.artifactId = artifactId;
      this.groupId = groupId;
    }

    private ArtifactId(final String id) {
      this(id, GROUP_ID);
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
