package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiDataBindingDependencyFacet;
import org.jboss.errai.forge.facet.module.ErraiDataBindingModuleFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ CoreFacet.class, ErraiIocFacet.class, ErraiDataBindingDependencyFacet.class,
    ErraiDataBindingModuleFacet.class })
public class ErraiDataBindingFacet extends BaseAggregatorFacet {
}
