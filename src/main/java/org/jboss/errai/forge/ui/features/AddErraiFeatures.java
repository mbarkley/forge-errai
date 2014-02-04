package org.jboss.errai.forge.ui.features;

import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections.Feature;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.command.UICommand;

public class AddErraiFeatures extends AbstractFeatureCommand implements
    UICommand {

  @Override
  protected FeatureFilter getFilter() {
    return new FeatureFilter() {
      @Override
      public boolean filter(Feature feature, Project project) {
        return project.hasFacet(feature.getFeatureClass());
      }
    };
  }

  @Override
  protected String getSelectionDescription() {
    return "The selected Errai Features will be added to your project,"
        + " including any necessary configurations and other required features.";
  }

  @Override
  protected String getSelectionLabel() {
    return "Select Errai Features to Add";
  }

}
