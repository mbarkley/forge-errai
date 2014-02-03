package org.jboss.errai.forge.ui.setup;

import javax.inject.Inject;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
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

public class NewModuleName extends AbstractUICommand implements UIWizardStep {

  private static final String DESCRIPTION =
          "The logical name of GWT module is the fully-qualified package"
                  + " and the name of the module file (without the gwt.xml suffix)."
                  + " For example, the module file org/jboss/errai/App.gwt.xml would"
                  + " have the logical name org.jboss.errai.App";

  @Inject
  private ProjectHolder holder;

  @Inject
  @WithAttributes(label = "Enter a Logical Module Name",
          required = true, description = DESCRIPTION)
  private UIInput<String> logicalModuleName;

  @Override
  @SuppressWarnings("unchecked")
  public NavigationResult next(UINavigationContext context) throws Exception {
    return context.navigateTo(ModuleRename.class);
  }

  @Override
  public void initializeUI(UIBuilder builder) throws Exception {
    builder.add(logicalModuleName);
  }

  @Override
  public Result execute(UIExecutionContext context) throws Exception {
    final ProjectConfig projectConfig = holder.getProject().getFacet(ProjectConfig.class);
    projectConfig.setProjectProperty(ProjectProperty.MODULE_LOGICAL, logicalModuleName.getValue());
    projectConfig.setProjectProperty(ProjectProperty.MODULE_FILE,
            ModuleSelect.moduleLogicalNameToFile(logicalModuleName.getValue(), holder.getProject()));

    return Results.success();
  }

}
