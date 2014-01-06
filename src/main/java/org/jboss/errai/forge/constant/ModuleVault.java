package org.jboss.errai.forge.constant;

public final class ModuleVault {
  
  public static enum Module {
    GwtUser("com.google.gwt.user.User"),
    ErraiCommon("org.jboss.errai.common.ErraiCommon"),
    ErraiBus("org.jboss.errai.bus.ErraiBus");
    
    private final String logicalName;
    private Module(final String logicalName) {
      this.logicalName = logicalName;
    }
    
    public String getLogicalName() {
      return logicalName;
    }
  }

}
