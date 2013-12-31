package org.jboss.errai.forge.facet.aggregate;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * Acts as top-level aggregator for pulling in other facet dependencies.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
abstract class BaseAggregatorFacet extends BaseFacet {
  // TODO Implement uninstall

  @Inject
  private BeanManager bm;
  
  @Override
  public boolean install() {
    return true;
  }

  @Override
  public boolean isInstalled() {
    if (getProject().hasFacet(getClass())) {
      return true;
    }
    else {
      /*
       * An aggregator facet is installed if all of its required facets are
       * installed.
       */
      final Class<? extends Facet>[] directDependencies = getClass().getAnnotation(RequiresFacet.class).value();
      for (final Class<? extends Facet> facetType : directDependencies) {
        @SuppressWarnings("unchecked")
        final Bean<? extends Facet> bean = (Bean<? extends Facet>) bm.getBeans(facetType).iterator().next();
        final CreationalContext<? extends Facet> ctx = bm.createCreationalContext(bean);
        Facet facet = (Facet) bm.getReference(bean, facetType, ctx);
        if (!facet.isInstalled()) {
          return false;
        }
      }
      
      return true;
    }
  }

}
