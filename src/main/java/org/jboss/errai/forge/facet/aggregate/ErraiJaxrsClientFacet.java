package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiJaxrsClientDependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ CoreFacet.class, ErraiJaxrsClientDependencyFacet.class, ErraiIocFacet.class,
    ErraiDataBindingFacet.class })
public class ErraiJaxrsClientFacet extends BaseAggregatorFacet {

  @Override
  public String getFeatureName() {
    return "Errai Jaxrs";
  }

  @Override
  public String getFeatureDescription() {
    return "A simple API for accessing server-side JAX-RS endpoints.";
  }

}
