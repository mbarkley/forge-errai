package org.jboss.errai.forge.ui.features;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.SerializableSet;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections.Feature;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.command.UICommand;

public class RemoveErraiFeatures extends AbstractFeatureCommand implements
    UICommand {

  @Override
  protected FeatureFilter getFilter() {
    return new FeatureFilter() {
      @Override
      public boolean filter(Feature feature, Project project) {
        final ProjectConfig projectConfig = project
            .getFacet(ProjectConfig.class);

        return project.hasFacet(feature.getFeatureClass())
            && projectConfig.getProjectProperty(
                ProjectProperty.INSTALLED_FEATURES, SerializableSet.class)
                .contains(feature.toString());
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

}
