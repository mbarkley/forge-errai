package org.jboss.errai.forge.facet.aggregate;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Singleton;

@Singleton
public class AggregatorFacetReflections {

  private final Map<Class<? extends BaseAggregatorFacet>, BaseAggregatorFacet> aggregators =
          new LinkedHashMap<Class<? extends BaseAggregatorFacet>, BaseAggregatorFacet>();

  public AggregatorFacetReflections() throws InstantiationException, IllegalAccessException {
    @SuppressWarnings("unchecked")
    final Class<? extends BaseAggregatorFacet>[] types = new Class[] {
            CoreFacet.class,
            ErraiMessagingFacet.class,
            ErraiIocFacet.class,
            ErraiCdiFacet.class,
            ErraiUiFacet.class,
            ErraiNavigationFacet.class,
            ErraiDataBindingFacet.class,
            ErraiJaxrsFacet.class,
            ErraiJpaClientFacet.class,
            ErraiJpaDatasyncFacet.class
    };
    
    for (int i = 0; i < types.length; i++) {
      aggregators.put(types[i], types[i].newInstance());
    }
  }

  public Map<Class<? extends BaseAggregatorFacet>, String> getNames() {
    final Map<Class<? extends BaseAggregatorFacet>, String> retVal =
            new LinkedHashMap<Class<? extends BaseAggregatorFacet>, String>(aggregators.size());
    
    for (final BaseAggregatorFacet facet : aggregators.values()) {
      retVal.put(facet.getClass(), facet.getFeatureName());
    }
    
    return retVal;
  }
  
  public Map<Class<? extends BaseAggregatorFacet>, String> getDescriptions() {
    final Map<Class<? extends BaseAggregatorFacet>, String> retVal =
            new LinkedHashMap<Class<? extends BaseAggregatorFacet>, String>(aggregators.size());
    
    for (final BaseAggregatorFacet facet : aggregators.values()) {
      retVal.put(facet.getClass(), facet.getFeatureDescription());
    }
    
    return retVal;
  }
}
