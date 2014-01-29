package org.jboss.errai.forge.test.base;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public abstract class ForgeTest {
  
  public static final String DEPENDENCY = "org.jboss.errai.forge:errai-forge-plugin";
  public static final String VERSION = "1.0.0-SNAPSHOT";
  public static final String FORGE_VERSION = "2.0.0.CR2";
  public static final String ADDON_GROUP = "org.jboss.forge.addon";

  @Inject
  protected ProjectFactory projectFactory;
  
  @Inject
  protected FacetFactory facetFactory;
  
  @Deployment(name = "ForgeTest")
  @Dependencies({
    @AddonDependency(name = DEPENDENCY, version = VERSION)
  })
  public static ForgeArchive getDeployment() {
    final ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class);
    
    archive.addBeansXML()
    .addAsAddonDependencies(
            AddonDependencyEntry.create(DEPENDENCY, VERSION)
    );
    
    return archive;
  }
  
  protected Project initializeJavaProject() {
    final Project project = projectFactory.createTempProject();
    
    return project;
  }

}
