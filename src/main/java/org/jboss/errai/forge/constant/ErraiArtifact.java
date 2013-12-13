package org.jboss.errai.forge.constant;


public final class ErraiArtifact {
  
  public static final String GROUP_ID = "org.jboss.errai";
  
  public static enum ArtifactId {
    ErraiCommon("errai-common");
    
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
