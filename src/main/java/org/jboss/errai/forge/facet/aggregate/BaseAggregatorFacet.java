package org.jboss.errai.forge.facet.aggregate;

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
  // TODO Implement uninstall

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
    final Class<? extends Facet>[] requirements = getClass().getAnnotation(RequiresFacet.class).value();

    for (int i = 0; i < requirements.length; i++) {
      if (!requirements[i].equals(CoreFacet.class) && BaseAggregatorFacet.class.isAssignableFrom(requirements[i])
              && project.hasFacet(requirements[i])) {
        final Facet facet = project.getFacet(requirements[i]);
        project.removeFacet(facet);
      }
    }

    return true;
  }
  
  /**
   * @return The name of the feature managed by this facet.
   */
  public abstract String getFeatureName();
  
  /**
   * @return The short name of the feature managed by this facet, used for referencing it through the shell.
   */
  public abstract String getShortName();
  
  /**
   * @return A short description of the feature managed by this facet.
   */
  public abstract String getFeatureDescription();
}
