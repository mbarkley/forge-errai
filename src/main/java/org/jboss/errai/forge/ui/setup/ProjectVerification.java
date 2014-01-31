package org.jboss.errai.forge.ui.setup;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.apache.maven.project.MavenProject;
import org.jboss.errai.forge.util.Condition;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizard;

public class ProjectVerification extends AbstractUICommand implements UIWizard {

  @Inject
  private Project project;
  
  private boolean error;

  @Inject
  @WithAttributes(label = "Confirm Errai Installation")
  private UIInput<Boolean> confirmation;

  @SuppressWarnings("unchecked")
  private final Condition<Project>[] conditions = new Condition[] {
      new Condition<Project>() {
        @Override
        public boolean isSatisfied(final Project subject) {
          return subject instanceof MavenProject;
        }

        @Override
        public String getShortDescription() {
          return "The given project must be a Maven project.";
        }
      }
  };

  @Override
  public Result execute(final UIExecutionContext context) throws Exception {
    return (isSuccessful(context.getUIContext())) ?
            Results.success() : Results.fail();
  }

  @Override
  public void initializeUI(final UIBuilder builder) throws Exception {
    final Collection<String> problems = getProblems();

    if (problems.isEmpty()) {
      confirmation.setDefaultValue(true);
      builder.add(confirmation);
    }
    else {
      final PrintStream err = builder.getUIContext().getProvider().getOutput().err();
      err.println(String.format(
              "There are some problems with your project (%s) preventing the installation of Errai with this Addon.",
              project.getRootDirectory().getUnderlyingResourceObject().getAbsolutePath()));
      for (final String problem : problems) {
        err.println(problem);
      }
      error = true;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public NavigationResult next(final UINavigationContext context) throws Exception {
    return (isSuccessful(context.getUIContext())) ? context.navigateTo(VersionSelect.class) : null;
  }
  
  private boolean isSuccessful(final UIContext context) {
    return !error && confirmation.getValue();
  }

  private Collection<String> getProblems() {
    final Collection<String> problems = new ArrayList<String>();

    for (int i = 0; i < conditions.length; i++) {
      if (!conditions[i].isSatisfied(project))
        problems.add(conditions[i].getShortDescription());
    }

    return problems;
  }

}
