package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiCdiClientDependencyFacet;
import org.jboss.errai.forge.facet.dependency.ErraiWeldIntegrationDependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ ErraiMessagingFacet.class, ErraiCdiClientDependencyFacet.class, ErraiWeldIntegrationDependencyFacet.class })
public class ErraiCdiFacet extends BaseAggregatorFacet {

}
