package org.jboss.errai.forge.example;
/**
 * @author pslegr
 */
public enum ErraiExampleEnum {
    ERRAI_BUS_EXAMPLE("errai-bus",ErraiBusExample.class),
    ERRAI_CDI_EXAMPLE("errai-cdi",ErraiCdiExample.class),
    ERRAI_JAXRS_EXAMPLE("errai-jaxrs",ErraiJaxrsExample.class),
    ERRAI_UI_EXAMPLE("errai-ui",ErraiUIExample.class);
    		
    private String name;
    private Class<? extends AbstractExample> example;

    private ErraiExampleEnum(String name, Class<? extends AbstractExample> example) {
        this.name = name;
        this.example = example;
    }

    public Class<? extends AbstractExample> getFacet() {
        return example;
    }

    @Override
    public String toString() {
        return name;
    }
}
