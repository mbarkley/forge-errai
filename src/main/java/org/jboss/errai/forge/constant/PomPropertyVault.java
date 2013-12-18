package org.jboss.errai.forge.constant;

public class PomPropertyVault {
  
  public static enum Property {
    JbossHome("errai.jboss.home"), ErraiVersion("errai.version"), DevContext("errai.dev.context");
    
    private String name;

    private Property(final String name) {
      this.name = name;
    }
    
    public String getName() {
      return name;
    }
    
    public String invoke() {
      return "${" + name + "}";
    }
  }

}
