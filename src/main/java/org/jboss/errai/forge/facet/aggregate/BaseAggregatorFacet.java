package org.jboss.errai.forge.facet.aggregate;

import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * Acts as top-level aggregator for pulling in other facet dependencies.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
abstract class BaseAggregatorFacet extends BaseFacet {
  // TODO Implement uninstall

  @Override
  public boolean install() {
    return true;
  }

  @Override
  public boolean isInstalled() {
    /*
     * An aggregator facet is installed if all of its required facets are
     * installed.
     */
    return getProject().hasAllFacets(getClass().getAnnotation(RequiresFacet.class).value());
  }

}
