package org.jboss.errai.forge.facet;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;


@RequiresFacet({ DependencyFacet.class, WebResourceFacet.class })
public class ErraiUIFacet extends ErraiBaseFacet{

	@Override
	void installErraiDeps() {
		  String erraiVersion = Versions.getInstance().getErrai_version();
		  String javaeeVersion = Versions.getInstance().getJavaee_version();
		  
	      List<? extends Dependency> dependencies = Arrays.asList(
	              DependencyBuilder.create("org.jboss.errai:errai-javaee-all:" + erraiVersion),
	              DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0:" + javaeeVersion).setScopeType("provided").setPackagingType("pom")
	      );

		   
	      DependencyFacet deps = project.getFacet(DependencyFacet.class);
	      for (Dependency dependency : dependencies) {
	         deps.addDirectDependency(dependency);
	      }
		
	}

	@Override
	boolean isFacetInstalled() {
        if (!project.hasFacet(ErraiUIFacet.class)) {
    		return false;
        }
		return true;
	}
}
