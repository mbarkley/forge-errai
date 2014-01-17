package org.jboss.errai.forge.facet.aggregate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.reflections.Reflections;

@Singleton
public class AggregatorFacetReflections {

  private final Map<Class<? extends BaseAggregatorFacet>, BaseAggregatorFacet> aggregators =
          new LinkedHashMap<Class<? extends BaseAggregatorFacet>, BaseAggregatorFacet>();

  @PostConstruct
  private void init() throws InstantiationException, IllegalAccessException {
    final Reflections reflections = new Reflections("org.jboss.errai.forge");
    final Set<Class<? extends BaseAggregatorFacet>> aggregatorTypes = reflections.getSubTypesOf(BaseAggregatorFacet.class);
    
    for (final Class<? extends BaseAggregatorFacet> clazz : aggregatorTypes) {
      aggregators.put(clazz, clazz.newInstance());
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
