package org.jboss.errai.forge.ui.features;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.SerializableSet;
import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections.Feature;
import org.jboss.errai.forge.facet.aggregate.BaseAggregatorFacet;
import org.jboss.forge.addon.facets.MutableFaceted;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;

public class RemoveErraiFeatures extends AbstractFeatureCommand {

  @Override
  protected FeatureFilter getFilter() {
    return new FeatureFilter() {
      @Override
      public boolean filter(Feature feature, Project project) {
        final ProjectConfig projectConfig = project
                .getFacet(ProjectConfig.class);
        final SerializableSet installed = projectConfig.getProjectProperty(
                ProjectProperty.INSTALLED_FEATURES, SerializableSet.class);

        return project.hasFacet(feature.getFeatureClass())
                && installed != null && installed.contains(feature.toString());
      }
    };
  }

  @Override
  protected String getSelectionDescription() {
    return "The selected Errai features will be removed from your project."
            + " Any additional features which were not explicitly installed and are no longer needed will also be removed.";
  }

  @Override
  protected String getSelectionLabel() {
    return "Select Errai Features to Remove";
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void performOperation(Project project, Feature feature) throws Exception {
    try {
      final MutableFaceted<ProjectFacet> mutable = (MutableFaceted<ProjectFacet>) project;
      final BaseAggregatorFacet facet = mutable.getFacet(feature.getFeatureClass());

      if (!mutable.uninstall(facet))
        throw new Exception(String.format("Could not uninstall %s from %s.", facet.getClass(), project));

      final ProjectConfig projectConfig = project.getFacet(ProjectConfig.class);
      SerializableSet installed = projectConfig.getProjectProperty(ProjectProperty.INSTALLED_FEATURES,
              SerializableSet.class);
      if (installed == null)
        installed = new SerializableSet();
      
      installed.remove(feature.toString());
      
      projectConfig.setProjectProperty(ProjectProperty.INSTALLED_FEATURES, installed);
    }
    catch (Exception e) {
      throw new Exception("Could not remove " + feature.getName(), e);
    }
  }

}
