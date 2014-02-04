package org.jboss.errai.forge.ui.features;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;

public class RemoveErraiFeatures extends AbstractFeatureCommand implements UICommand {

  @Override
  public void initializeUI(UIBuilder builder) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public Result execute(UIExecutionContext context) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected boolean isProjectRequired() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected ProjectFactory getProjectFactory() {
    // TODO Auto-generated method stub
    return null;
  }

}
