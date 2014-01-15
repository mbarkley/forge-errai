package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiIocDependencyFacet;
import org.jboss.errai.forge.facet.resource.ErraiAppPropertiesFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * Aggregator facet for Errai IOC. Adds errai-ioc dependency and ensures
 * ErraiApp.properties is in resources folder.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ CoreFacet.class, ErraiIocDependencyFacet.class, ErraiAppPropertiesFacet.class })
public class ErraiIocFacet extends BaseAggregatorFacet {
}
