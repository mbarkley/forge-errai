package org.jboss.errai.forge.ui.setup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class VersionSelect extends AbstractUICommand implements UIWizardStep {

  @Inject
  private Project project;
  
  @Inject
  private FacetFactory facetFactory;

  @Inject
  @WithAttributes(label = "Select an Errai Version", required = true,
          requiredMessage = "An Errai version must be given to proceed.")
  private UISelectOne<String> versionSelect;

  @Override
  @SuppressWarnings("unchecked")
  public NavigationResult next(UINavigationContext context) throws Exception {
    return context.navigateTo(ModuleSelect.class);
  }

  @Override
  public void initializeUI(UIBuilder builder) throws Exception {
    versionSelect.setValueChoices(getValidErraiVersions());
  }

  @Override
  public Result execute(UIExecutionContext context) throws Exception {
    final ProjectConfig projectConfig = facetFactory.install(project, ProjectConfig.class);
    projectConfig.setProjectProperty(ProjectProperty.ERRAI_VERSION, versionSelect.getValue());

    return Results.success();
  }

  private Collection<String> getValidErraiVersions() {
    final DependencyFacet depFacet = project.getFacet(DependencyFacet.class);
    final Dependency erraiDep = DependencyBuilder.create(DependencyArtifact.ErraiParent.toString());

    final List<String> versions = new ArrayList<String>();
    for (final Coordinate coord : depFacet.resolveAvailableVersions(erraiDep)) {
      if (isValidVersion(coord.getVersion()))
        versions.add(coord.getVersion());
    }

    return versions;
  }

  private boolean isValidVersion(final String version) {
    final Integer majorVersion;

    try {
      majorVersion = Integer.valueOf(version.substring(0, 1));
    }
    catch (NumberFormatException e) {
      return false;
    }

    return (majorVersion > 3
    || (majorVersion == 3
            && (version.contains("SNAPSHOT")) || version.compareTo("3.0.0.20131205-M3") > 0));
  }

}
