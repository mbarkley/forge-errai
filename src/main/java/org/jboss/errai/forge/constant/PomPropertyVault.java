package org.jboss.errai.forge.constant;

public class PomPropertyVault {
  
  public static enum Property {
    JbossHome("errai.jboss.home");
    
    private String name;

    private Property(final String name) {
      this.name = name;
    }
    
    public String getName() {
      return name;
    }
  }

}
