package org.jboss.errai.forge.constant;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
public final class ModuleVault {
  
  /**
   * An enumeration of GWT modules used by various facets.
   * 
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static enum Module {
    GwtUser("com.google.gwt.user.User"),
    ErraiCommon("org.jboss.errai.common.ErraiCommon"),
    ErraiBus("org.jboss.errai.bus.ErraiBus"),
    ErraiIoc("org.jboss.errai.ioc.Container"),
    ErraiCdi("org.jboss.errai.enterprise.CDI"),
    ErraiUi("org.jboss.errai.ui.UI"),
    ErraiNavigation("org.jboss.errai.ui.nav.Navigation");
    
    private final String logicalName;
    private Module(final String logicalName) {
      this.logicalName = logicalName;
    }
    
    /**
     * @return The fully qualified logical name of this GWT module.
     */
    public String getLogicalName() {
      return logicalName;
    }
  }

}
