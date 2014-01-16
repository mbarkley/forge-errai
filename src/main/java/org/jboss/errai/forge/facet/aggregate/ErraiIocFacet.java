package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiIocDependencyFacet;
import org.jboss.errai.forge.facet.module.ErraiIocModulFacet;
import org.jboss.errai.forge.facet.resource.ErraiAppPropertiesFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * Aggregator facet for Errai IOC. Adds errai-ioc dependency and ensures
 * ErraiApp.properties is in resources folder.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ CoreFacet.class, ErraiIocDependencyFacet.class, ErraiIocModulFacet.class,
    ErraiAppPropertiesFacet.class })
public class ErraiIocFacet extends BaseAggregatorFacet {

  @Override
  public String getFeatureName() {
    return "Errai IOC";
  }

  @Override
  public String getFeatureDescription() {
    return "An IOC container allowing dependency injection in GWT-compiled code.";
  }
}
