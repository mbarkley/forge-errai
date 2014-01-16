package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiUiDependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ CoreFacet.class, ErraiUiDependencyFacet.class, ErraiIocFacet.class })
public class ErraiUiFacet extends BaseAggregatorFacet {
}
