package org.jboss.errai.forge.facet.aggregate;

import org.jboss.errai.forge.facet.dependency.ErraiBuildDependencyFacet;
import org.jboss.errai.forge.facet.plugin.CleanPluginFacet;
import org.jboss.errai.forge.facet.plugin.CompilerPluginFacet;
import org.jboss.errai.forge.facet.plugin.DependencyPluginFacet;
import org.jboss.errai.forge.facet.plugin.GwtPluginFacet;
import org.jboss.errai.forge.facet.plugin.JbossPluginFacet;
import org.jboss.errai.forge.facet.plugin.WarPluginFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({ ErraiBuildDependencyFacet.class, CleanPluginFacet.class, CompilerPluginFacet.class,
    DependencyPluginFacet.class, GwtPluginFacet.class, JbossPluginFacet.class, WarPluginFacet.class })
public class CoreFacet extends BaseAggregatorFacet {
}