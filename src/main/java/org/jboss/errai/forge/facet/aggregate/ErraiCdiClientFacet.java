package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiCdiClientDependencyFacet;
import org.jboss.errai.forge.facet.module.ErraiCdiModuleFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * An aggregator facet for errai-cdi-client. This enables client-side CDI
 * events.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ ErraiMessagingFacet.class, ErraiIocFacet.class, ErraiCdiClientDependencyFacet.class,
    ErraiCdiModuleFacet.class })
public class ErraiCdiClientFacet extends BaseAggregatorFacet {

  @Override
  public String getFeatureName() {
    return "Errai Client-Side CDI";
  }

  @Override
  public String getFeatureDescription() {
    return "Allows the use of CDI Events and Observers in GWT-compiled code.";
  }

  @Override
  public String getShortName() {
    return "cdi-client";
  }
}
