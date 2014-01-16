package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiWeldIntegrationDependencyFacet;
import org.jboss.errai.forge.facet.resource.CdiWebXmlFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * An aggregator facet for Errai CDI. This enables CDI events to be observed
 * between the client and server.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ CoreFacet.class, ErraiCdiClientFacet.class, ErraiWeldIntegrationDependencyFacet.class,
    CdiWebXmlFacet.class })
public class ErraiCdiFacet extends BaseAggregatorFacet {
}
