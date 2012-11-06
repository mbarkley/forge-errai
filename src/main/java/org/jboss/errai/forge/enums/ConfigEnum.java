package org.jboss.errai.forge.enums;



/**
 * @author pslegr
 */
public enum ConfigEnum {
    CONF_NONSERIALIZABLE("nonserializableTypes","errai.marshalling.nonserializableTypes"),
    CONF_SERIALIZABLE("serializableTypes","errai.marshalling.serializableTypes"),
    CONF_MAPPING_ALLIASES("mappingAliases","errai.marshalling.mappingAliases");
    private String name;
    private String key;

    private ConfigEnum(String name, String key) {
        this.name = name;
        this.key = key;
    }

    @Override
    public String toString() {
        return name;
    }

	public String getKey() {
		return key;
	}
}
