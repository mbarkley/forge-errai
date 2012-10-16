package org.jboss.errai.forge.enums;

/**
 * @author pslegr
 */
public enum ErraiViaDefinitionEnum {
    ANNOTATION("ANNOTATION"),
    ERRAI_CONFIG("ERRAI_CONFIG");

    private String name;

    private ErraiViaDefinitionEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
