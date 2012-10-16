package org.jboss.errai.forge.enums;

/**
 * @author pslegr
 */
public enum ErraiExamplesCommandsEnum {
    ERRAI_EXAMPLES_HELP("help"),
    ERRAI_EXAMPLES_SETUP("setup"),
    ERRAI_EXAMPLES_INSTALL_ERRAI_BUS("install-errai-bus"),
    ERRAI_EXAMPLES_INSTALL_ERRAI_CDI("install-errai-CDI"),
    ERRAI_EXAMPLES_INSTALL_ERRAI_JAXRS("install-errai-jaxrs"),
    ERRAI_EXAMPLES_INSTALL_ERRAI_UI("install-errai-ui"),
    ERRAI_EXAMPLES_UNINSTALL_ERRAI_BUS("uninstall-errai-bus"),
    ERRAI_EXAMPLES_UNINSTALL_ERRAI_CDI("uninstall-errai-CDI"),
    ERRAI_EXAMPLES_UNINSTALL_ERRAI_JAXRS("uninstall-errai-jaxrs"),
    ERRAI_EXAMPLES_UNINSTALL_ERRAI_UI("uninstall-errai-ui");
    

    private String name;

    private ErraiExamplesCommandsEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
