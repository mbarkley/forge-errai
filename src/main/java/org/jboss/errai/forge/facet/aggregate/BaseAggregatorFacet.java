package org.jboss.errai.forge.facet.aggregate;

import org.jboss.forge.project.facets.BaseFacet;

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
    return getProject().hasFacet(getClass());
  }

}
