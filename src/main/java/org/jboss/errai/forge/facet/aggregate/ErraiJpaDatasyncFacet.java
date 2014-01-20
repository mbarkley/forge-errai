package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiJpaDatasyncDependencyFacet;
import org.jboss.errai.forge.facet.module.ErraiJpaDatasyncModuleFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ CoreFacet.class, ErraiJpaClientFacet.class, ErraiCdiClientFacet.class,
    ErraiJpaDatasyncDependencyFacet.class, ErraiJpaDatasyncModuleFacet.class })
public class ErraiJpaDatasyncFacet extends BaseAggregatorFacet {

  @Override
  public String getFeatureName() {
    return "Errai JPA Datasync";
  }

  @Override
  public String getFeatureDescription() {
    return "Synchronize client-side persisted data with server-side JPA.";
  }

  @Override
  public String getShortName() {
    return "jpa-datasync";
  }
}
