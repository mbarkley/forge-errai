package org.jboss.errai.forge.facet.aggregate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Singleton;

@Singleton
public class AggregatorFacetReflections {

  private final Map<String, Feature> features =
          new LinkedHashMap<String, Feature>();

  public static class Feature {
    private final String shortName;
    private final String name;
    private final String description;
    private final Class<? extends BaseAggregatorFacet> clazz;

    public String getShortName() {
      return shortName;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }
    
    public Class<? extends BaseAggregatorFacet> getFeatureClass() {
      return clazz;
    }

    @Override
    public String toString() {
      return getShortName();
    }

    private Feature(final String shortName, final String name, final String description,
            final Class<? extends BaseAggregatorFacet> clazz) {
      this.shortName = shortName;
      this.name = name;
      this.description = description;
      this.clazz = clazz;
    }

    private Feature(final BaseAggregatorFacet facet) {
      this(facet.getShortName(), facet.getFeatureName(), facet.getFeatureDescription(), facet.getClass());
    }
  }

  public AggregatorFacetReflections() throws InstantiationException, IllegalAccessException {
    @SuppressWarnings("unchecked")
    final Class<? extends BaseAggregatorFacet>[] types = new Class[] {
        ErraiMessagingFacet.class,
        ErraiIocFacet.class,
        ErraiCdiFacet.class,
        ErraiUiFacet.class,
        ErraiNavigationFacet.class,
        ErraiDataBindingFacet.class,
        ErraiJaxrsFacet.class,
        ErraiJpaClientFacet.class,
        ErraiJpaDatasyncFacet.class,
        ErraiCordovaFacet.class
    };

    for (int i = 0; i < types.length; i++) {
      final BaseAggregatorFacet instance = types[i].newInstance();
      features.put(instance.getShortName(), new Feature(instance));
    }
  }

  public Feature getFeature(final String shortName) {
    return features.get(shortName);
  }

  public boolean hasFeature(final String shortName) {
    return getFeature(shortName) != null;
  }

  public Iterable<Feature> iterable() {
    return Collections.unmodifiableCollection(features.values());
  }
}
