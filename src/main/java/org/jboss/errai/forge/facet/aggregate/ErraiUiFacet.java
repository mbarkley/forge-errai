package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiUiDependencyFacet;
import org.jboss.errai.forge.facet.module.ErraiUiModuleFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ CoreFacet.class, ErraiIocFacet.class, ErraiUiDependencyFacet.class, ErraiUiModuleFacet.class })
public class ErraiUiFacet extends BaseAggregatorFacet {
}
