package org.jboss.errai.forge.facet.aggregate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.ProjectConfigFactory;
import org.jboss.errai.forge.config.SerializableSet;
import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections.Feature;
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * Acts as top-level aggregator for pulling in other facet dependencies.
 * Concrete subclasses should be used to simplify the process of installing
 * complex combinations of facets.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class BaseAggregatorFacet extends BaseFacet {

  @Inject
  private AggregatorFacetReflections reflections;

  @Inject
  private ProjectConfigFactory configFactory;

  @Override
  public boolean install() {
    return true;
  }

  @Override
  public boolean isInstalled() {
    /*
     * An aggregator facet is installed if all of its required facets are
     * installed. There is no need to do a recursive traversal, as the presence
     * of direct dependencies in the project means that forge has already
     * verified the installation of transitively required facets.
     */
    return getProject().hasAllFacets(getClass().getAnnotation(RequiresFacet.class).value());
  }

  @Override
  public boolean uninstall() {
    final ProjectConfig config = configFactory.getProjectConfig(project);
    final SerializableSet installedFeatureNames = config.getProjectProperty(ProjectProperty.INSTALLED_FEATURES,
            SerializableSet.class);
    final Set<Class<? extends Facet>> directlyInstalled = new HashSet<Class<? extends Facet>>();

    for (final String featureName : installedFeatureNames) {
      directlyInstalled.add(reflections.getFeature(featureName).getFeatureClass());
    }
    directlyInstalled.remove(getClass());

    final Set<Class<? extends Facet>> toUninstall = traverseUninstallable(directlyInstalled);

    /*
     * Traverse requirements for remaining aggregators to keep facets that are
     * still required.
     */
    final Collection<Class<? extends Facet>> installedFeatures = new ArrayList<Class<? extends Facet>>();
    for (final Feature feature : reflections.iterable()) {
      final Class<? extends BaseAggregatorFacet> featureClass = feature.getFeatureClass();
      if (project.hasFacet(featureClass) && !featureClass.equals(getClass()) && !toUninstall.contains(featureClass)) {
        installedFeatures.add(featureClass);
      }
    }
    installedFeatures.add(CoreFacet.class);
    toUninstall.remove(CoreFacet.class);

    keepRequired(installedFeatures, toUninstall);

    for (final Class<? extends Facet> facetType : toUninstall) {
      project.removeFacet(project.getFacet(facetType));
    }

    return true;
  }

  /**
   * Traverse the required facets of the featureClasses, and remove all of the
   * traversed facets from the removable set.
   */
  private void keepRequired(final Collection<Class<? extends Facet>> featureClasses,
          final Set<Class<? extends Facet>> removable) {
    final Set<Class<? extends Facet>> traversed = new HashSet<Class<? extends Facet>>();
    final Queue<Class<? extends Facet>> toVisit = new LinkedList<Class<? extends Facet>>();
    toVisit.addAll(featureClasses);

    while (!toVisit.isEmpty()) {
      final Class<? extends Facet> cur = toVisit.poll();
      if (!traversed.contains(cur)) {
        traversed.add(cur);

        if (cur.isAnnotationPresent(RequiresFacet.class)) {
          final Class<? extends Facet>[] requirements = cur.getAnnotation(RequiresFacet.class).value();
          for (int i = 0; i < requirements.length; i++) {
            if (!traversed.contains(requirements[i])) {
              toVisit.add(requirements[i]);
              removable.remove(requirements[i]);
            }
          }
        }
      }
    }
  }

  /**
   * Traverse the required facets of this class and add them to collection. But
   * ignore required facets in the intentionally installed facet.
   */
  private Set<Class<? extends Facet>> traverseUninstallable(final Set<Class<? extends Facet>> intentionallyInstalled) {
    final Set<Class<? extends Facet>> traversed = new HashSet<Class<? extends Facet>>();

    final Queue<Class<? extends Facet>> toVisit = new LinkedList<Class<? extends Facet>>();
    toVisit.addAll(Arrays.asList(getClass().getAnnotation(RequiresFacet.class).value()));

    while (!toVisit.isEmpty()) {
      final Class<? extends Facet> cur = toVisit.poll();
      if (!traversed.contains(cur)
              && (!BaseAggregatorFacet.class.isAssignableFrom(cur) || !intentionallyInstalled.contains(cur))
              // Only add errai facets to be uninstalled
              && (BaseAggregatorFacet.class.isAssignableFrom(cur) || AbstractBaseFacet.class.isAssignableFrom(cur))) {
        traversed.add(cur);

        if (cur.isAnnotationPresent(RequiresFacet.class)) {
          final Class<? extends Facet>[] requirements = cur.getAnnotation(RequiresFacet.class).value();
          for (int i = 0; i < requirements.length; i++) {
            if (!traversed.contains(requirements[i])) {
              toVisit.add(requirements[i]);
            }
          }
        }
      }
    }

    return traversed;
  }

  /**
   * @return The name of the feature managed by this facet.
   */
  public abstract String getFeatureName();

  /**
   * @return The short name of the feature managed by this facet, used for
   *         referencing it through the shell.
   */
  public abstract String getShortName();

  /**
   * @return A short description of the feature managed by this facet.
   */
  public abstract String getFeatureDescription();
}
