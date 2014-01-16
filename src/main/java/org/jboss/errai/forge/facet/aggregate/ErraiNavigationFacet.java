package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiNavigationDependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ CoreFacet.class, ErraiUiFacet.class, ErraiNavigationDependencyFacet.class })
public class ErraiNavigationFacet extends BaseAggregatorFacet {
}
