package org.jboss.errai.forge.facet;


/**
 * @author pslegr
 */
public enum ErraiFacets {
    ERRAI_BUS_FACET("errai-bus",ErraiBusFacet.class),
    ERRAI_CDI_FACET("errai-cdi",ErraiCDIFacet.class),
    ERRAI_JAXRS_FACET("errai-jaxrs",ErraiJaxrsFacet.class),
    ERRAI_UI_FACET("errai-ui",ErraiUIFacet.class);
    		
    private String name;
    private Class<? extends ErraiBaseFacet> facet;

    private ErraiFacets(String name, Class<? extends ErraiBaseFacet> facet) {
        this.name = name;
        this.facet = facet;
    }

    public Class<? extends ErraiBaseFacet> getFacet() {
        return facet;
    }

    @Override
    public String toString() {
        return name;
    }
}
