package org.jboss.errai.forge.ui.setup;

import javax.inject.Inject;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class ModuleRename extends AbstractUICommand implements UIWizardStep {

  @Inject
  private Project project;

  @Inject
  @WithAttributes(label = "Enter a Short Name for the GWT Module", required = false, defaultValue = "",
          description = "This option can be used to give the module a shorter more convenient name than the logical name.")
  private UIInput<String> moduleName;

  @Override
  @SuppressWarnings("unchecked")
  public NavigationResult next(UINavigationContext context) throws Exception {
//    return context.navigateTo(FeatureSelect.class);
    return null;
  }

  @Override
  public void initializeUI(UIBuilder builder) throws Exception {
    builder.add(moduleName);
  }

  @Override
  public Result execute(UIExecutionContext context) throws Exception {
    final ProjectConfig projectConfig = project.getFacet(ProjectConfig.class);
    final String logicalName = projectConfig.getProjectProperty(ProjectProperty.MODULE_LOGICAL, String.class);

    String newName = moduleName.getValue();

    if (newName == null || newName.equals("")) {
      newName = logicalName;
    }
    
    projectConfig.setProjectProperty(ProjectProperty.MODULE_NAME, newName);

    return Results.success();
  }

}
