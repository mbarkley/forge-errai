package org.jboss.errai.forge.enums;

import org.jboss.errai.forge.template.Velocity;


/**
 * @author pslegr
 */
public enum ErraiGeneratorCommandsEnum {
    ERRAI_BUS_GENERATE_REMOTES_FROM_ALL_SERVICE_CLASSES("rpc-generate-remotes-for-all-services","bus_remote_interface_for_service_class"),
    ERRAI_BUS_GENERATE_REMOTE_FROM_SERVICE_CLASS("rpc-generate-remote-from-service","bus_remote_interface_for_service_class"),
    // no template for marshaling @Portable generation
    ERRAI_MARSHALING_SET_PORTABLE_RECURSIVE("set-portable-recursive",""),
    ERRAI_MARSHALING_SET_PORTABLE("set-portable",""),
    ERRAI_MARSHALING_SET_PORTABLE_RECURSIVE_VIA_CONFIG("set-portable-recursive",""),
    ERRAI_MARSHALING_SET_PORTABLE_VIA_CONFIG("set-portable",""),
    ERRAI_MARSHALING_IMMUTABLE_BUILDER_RECURSIVE("immutable-builder-recursive",""),
    ERRAI_MARSHALING_IMMUTABLE_BUILDER("immutable-builder","");

    private String name;
    private String templateFileName;

    private ErraiGeneratorCommandsEnum(String name, String templateFileName) {
        this.name = name;
        this.templateFileName = templateFileName;
    }

    @Override
    public String toString() {
        return name;
    }

	public String getTemplateFileName() {
		return templateFileName;
	}
	
	public String getTemplateFQName() {
		return templateFileName + Velocity.TEMPLATE_TEMPLATES_SUFF;
	}
	

}
