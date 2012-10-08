package org.jboss.errai.forge.facet;

import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;


@RequiresFacet({ DependencyFacet.class, WebResourceFacet.class })
public class ErraiBusFacet extends ErraiBaseFacet{

	@Override
	void installErraiFacetSpecifics() {
		// TODO Auto-generated method stub
		
	}

	@Override
	boolean isFacetInstalled() {
        if (!project.hasFacet(ErraiBusFacet.class)) {
    		return false;
        }
		return true;
	}

}
