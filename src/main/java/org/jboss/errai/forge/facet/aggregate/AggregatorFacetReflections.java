package org.jboss.errai.forge.facet.aggregate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.jboss.errai.forge.plugin.Main;

/**
 * A class for querying meta-data on of top-level aggregator facets.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Singleton
public class AggregatorFacetReflections {

  private final Map<String, Feature> features =
          new LinkedHashMap<String, Feature>();

  /**
   * Represents a feature that can be installed with the {@link Main plugin}.
   * 
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static class Feature {
    private final String shortName;
    private final String name;
    private final String description;
    private final Class<? extends BaseAggregatorFacet> clazz;

    /**
     * @return The short name of this feature, used for referencing this feature
     *         in the forge command line.
     */
    public String getShortName() {
      return shortName;
    }

    /**
     * @return The full descriptive name of this feature.
     */
    public String getName() {
      return name;
    }

    /**
     * @return A short one- or two-sentence description of this feature.
     */
    public String getDescription() {
      return description;
    }

    /**
     * @return The class extending {@link BaseAggregatorFacet} associated with
     *         this feature.
     */
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

  /**
   * Get a {@link Feature} by it's {@linkplain Feature#getShortName() short
   * name}.
   * 
   * @param shortName
   *          The {@linkplain Feature#getShortName() short name} of the feature
   *          to retrieve.
   * @return The {@link Feature} with a short name matching the one given, or
   *         {@literal null} if none exists.
   */
  public Feature getFeature(final String shortName) {
    return features.get(shortName);
  }

  /**
   * Check if a feature exists.
   * 
   * @param shortName
   *          The {@linkplain Feature#getShortName() short name} of a feature.
   * @return True iff there is a feature matching the given short name.
   */
  public boolean hasFeature(final String shortName) {
    return getFeature(shortName) != null;
  }

  /**
   * @return An {@link Iterable} object with all the {@link Feature Features}.
   */
  public Iterable<Feature> iterable() {
    return Collections.unmodifiableCollection(features.values());
  }
}
