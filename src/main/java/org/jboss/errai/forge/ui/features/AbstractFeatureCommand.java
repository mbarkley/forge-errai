package org.jboss.errai.forge.ui.features;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections;
import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections.Feature;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.result.Result;

abstract class AbstractFeatureCommand extends AbstractProjectCommand implements UICommand {
  
  protected static interface FeatureFilter {
    /**
     * True iff the feature should be displayed.
     */
    public boolean filter(final Feature feature, final Project project);
  }
  
  
  @Inject
  private ProjectFactory projectFactory;
  
  @Inject
  private UISelectMany<Feature> featureSelect;
  
  @Inject
  private AggregatorFacetReflections facetReflections;

  @Override
  public void initializeUI(UIBuilder builder) throws Exception {
    final Project project = getSelectedProject(builder.getUIContext());
    
    featureSelect.setLabel(getSelectionLabel());
    featureSelect.setDescription(getSelectionDescription());
    
    final List<Feature> features = new ArrayList<Feature>();
    final FeatureFilter filter = getFilter();
    for (final Feature feature : facetReflections.iterable()) {
      if (filter.filter(feature, project)) {
        features.add(feature);
      }
    }
    featureSelect.setValueChoices(features);
    
    
  }
  
  protected abstract FeatureFilter getFilter();
  
  protected abstract String getSelectionDescription();
  
  protected abstract String getSelectionLabel();

  @Override
  public Result execute(UIExecutionContext context) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected boolean isProjectRequired() {
    return true;
  }

  @Override
  protected ProjectFactory getProjectFactory() {
    return projectFactory;
  }

}
