package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiBuildDependencyFacet;
import org.jboss.errai.forge.facet.dependency.ErraiBusDependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * Aggregator facet for Errai Messaging.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ CoreFacet.class, ErraiBusDependencyFacet.class, ErraiBuildDependencyFacet.class })
public class ErraiMessagingFacet extends BaseAggregatorFacet {
}
