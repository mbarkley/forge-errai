package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiBuildDependencyFacet;
import org.jboss.errai.forge.facet.module.ModuleCoreFacet;
import org.jboss.errai.forge.facet.plugin.CleanPluginFacet;
import org.jboss.errai.forge.facet.plugin.CompilerPluginFacet;
import org.jboss.errai.forge.facet.plugin.DependencyPluginFacet;
import org.jboss.errai.forge.facet.plugin.GwtPluginFacet;
import org.jboss.errai.forge.facet.plugin.JbossPluginFacet;
import org.jboss.errai.forge.facet.plugin.WarPluginFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * Aggregates core facets required by all other facet aggregators. Installing
 * this facet will add all the necessary dependencies, profile, and plugin
 * configurations to run a GWT/Errai project in development mode or compile to production mode.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RequiresFacet({ ErraiBuildDependencyFacet.class, CleanPluginFacet.class, CompilerPluginFacet.class,
    DependencyPluginFacet.class, GwtPluginFacet.class, JbossPluginFacet.class, WarPluginFacet.class,
    ModuleCoreFacet.class })
public class CoreFacet extends BaseAggregatorFacet {
}
