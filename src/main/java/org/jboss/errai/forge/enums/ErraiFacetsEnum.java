package org.jboss.errai.forge.enums;

import org.jboss.errai.forge.facet.ErraiBusFacet;
import org.jboss.errai.forge.facet.ErraiExampleFacet;
import org.jboss.forge.project.facets.BaseFacet;



/**
 * @author pslegr
 */
public enum ErraiFacetsEnum {
    ERRAI_EXAMPLES("examples",ErraiExampleFacet.class),
    ERRAI_BUS("errai-bus",ErraiBusFacet.class);
    
    
    private String name;
    private Class<? extends BaseFacet> facet;

    private ErraiFacetsEnum(String name, Class<? extends BaseFacet> facet) {
        this.name = name;
        this.facet = facet;
    }

    public Class<? extends BaseFacet> getFacet() {
        return facet;
    }

    @Override
    public String toString() {
        return name;
    }
}
