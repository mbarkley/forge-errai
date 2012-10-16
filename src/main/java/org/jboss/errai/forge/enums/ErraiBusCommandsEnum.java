package org.jboss.errai.forge.enums;

/**
 * @author pslegr
 */
public enum ErraiBusCommandsEnum {
    ERRAI_BUS_SETUP("setup"),
    ERRAI_BUS_GENERATE_EMPTY_SERVICE_CLASS("rpc-generate-empty-service-impl"),
    ERRAI_BUS_GENERATE_SIMPLE_SERVICE_CLASS("rpc-generate-simple-service-impl"),
    ERRAI_BUS_GENERATE_REMOTE_FROM_SERVICE_CLASS("rpc-generate-remote-from-service"),
    ERRAI_BUS_GENERATE_REMOTES_FROM_ALL_SERVICE_CLASSES("rpc-generate-remotes-for-all-services"),
    ERRAI_BUS_RPC_INVOKE_ENDPOINT("rpc-invoke-endpoint");

    private String name;

    private ErraiBusCommandsEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
