package org.jboss.errai.forge.facet.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

@Stereotype
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@RequiresFacet({ MavenCoreFacet.class, MavenPluginFacet.class, DependencyFacet.class, CoreBuildFacet.class })
public @interface RequiresCore {
}
