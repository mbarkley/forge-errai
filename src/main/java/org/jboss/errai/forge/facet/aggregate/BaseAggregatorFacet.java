package org.jboss.errai.forge.facet.aggregate;

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
import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
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

  @SuppressWarnings("serial")
  public static class UninstallationExecption extends Exception {

    private UninstallationExecption(final Class<? extends Facet> dependentFacetType, final Project project,
            final BaseAggregatorFacet toUninstall) {
      super(generateMessage(dependentFacetType, project, toUninstall));
    }

    private static String generateMessage(final Class<? extends Facet> facetType, final Project project,
            final BaseAggregatorFacet toUninstall) {
      if (BaseAggregatorFacet.class.isAssignableFrom(facetType) && project.hasFacet(facetType)) {
        final BaseAggregatorFacet facet = BaseAggregatorFacet.class.cast(project.getFacet(facetType));

        return String.format("%s (%s) still requires %s.", facet.getFeatureName(), facet.getShortName(),
                toUninstall.getFeatureName());
      }
      else {
        return String.format("The facet %s still requires %s.", facetType.getSimpleName());
      }
    }
  }

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
    return true;
  }

  /**
   * Uninstall this facet and all required facets which will not be otherwise
   * required after this facet is removed.
   * 
   * @return True on successful uninstallation.
   * @throws UninstallationExecption
   *           Thrown if this class is still required by another facet.
   */
  public boolean uninstallRequirements() throws UninstallationExecption {
    final ProjectConfig config = configFactory.getProjectConfig(project);
    final SerializableSet installedFeatureNames = config.getProjectProperty(ProjectProperty.INSTALLED_FEATURES,
            SerializableSet.class);
    final Set<Class<? extends Facet>> directlyInstalled = new HashSet<Class<? extends Facet>>();

    for (final String featureName : installedFeatureNames) {
      directlyInstalled.add(reflections.getFeature(featureName).getFeatureClass());
    }
    directlyInstalled.remove(getClass());
    directlyInstalled.add(CoreFacet.class);

    final Set<Class<? extends Facet>> toUninstall = traverseUninstallable(directlyInstalled);

    keepRequired(directlyInstalled, toUninstall);

    for (final Class<? extends Facet> facetType : toUninstall) {
      if (project.hasFacet(facetType))
        project.removeFacet(project.getFacet(facetType));
    }

    return true;
  }

  /**
   * Traverse the required facets of the featureClasses, and remove all of the
   * traversed facets from the removable set.
   * 
   * @throws UninstallationExecption
   *           Thrown if this feature is still required by another facet.
   */
  private void keepRequired(final Collection<Class<? extends Facet>> featureClasses,
          final Set<Class<? extends Facet>> removable) throws UninstallationExecption {
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
              // Some other feature still depends on this class...
              if (requirements[i].equals(getClass()))
                throw new UninstallationExecption(cur, project, this);

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
    toVisit.add(getClass());

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
