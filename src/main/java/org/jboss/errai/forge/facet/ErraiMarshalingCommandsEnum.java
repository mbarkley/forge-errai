package org.jboss.errai.forge.facet;

/**
 * @author pslegr
 */
public enum ErraiMarshalingCommandsEnum {
    ERRAI_MARSHALING_SETUP("setup"),
    ERRAI_MARSHALING_SET_PORTABLE("set-portable"),
    ERRAI_MARSHALING_IMMUTABLE_BUILDER("immutable-builder");

    private String name;

    private ErraiMarshalingCommandsEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
