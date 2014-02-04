package org.jboss.errai.forge.ui.features;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.config.SerializableSet;
import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections;
import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections.Feature;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

abstract class AbstractFeatureCommand extends AbstractProjectCommand implements
    UICommand {

  protected static interface FeatureFilter {
    /**
     * True iff the feature should be displayed.
     */
    public boolean filter(final Feature feature, final Project project);
  }

  protected class FeatureConverter implements Converter<String, Feature> {

    @Override
    public Feature convert(final String shortName) {
      return facetReflections.getFeature(shortName);
    }

  }

  @Inject
  private ProjectFactory projectFactory;

  @Inject
  private FacetFactory facetFactory;

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
    featureSelect.setValueConverter(new FeatureConverter());
  }

  protected abstract FeatureFilter getFilter();

  protected abstract String getSelectionDescription();

  protected abstract String getSelectionLabel();

  @Override
  public Result execute(UIExecutionContext context) throws Exception {
    final Project project = getSelectedProject(context.getUIContext());
    final Iterable<Feature> features = featureSelect.getValue();

    for (final Feature feature : features) {
      try {
        install(project, feature);
      } catch(IllegalStateException e) {
        return Results.fail(String.format("Could not install %s",
            feature.getShortName()), e);
      }
    }

    return Results.success();
  }

  private void install(final Project project, final Feature feature) {
    facetFactory.install(project, feature.getFeatureClass());

    final ProjectConfig projectConfig = project.getFacet(ProjectConfig.class);
    final SerializableSet installed = projectConfig.getProjectProperty(ProjectProperty.INSTALLED_FEATURES, SerializableSet.class);
    installed.add(feature.toString());
    
    projectConfig.setProjectProperty(ProjectProperty.INSTALLED_FEATURES, installed);
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
