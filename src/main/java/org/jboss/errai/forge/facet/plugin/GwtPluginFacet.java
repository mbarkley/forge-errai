package org.jboss.errai.forge.facet.plugin;

import java.util.ArrayList;
import java.util.Arrays;

import org.jboss.errai.forge.constant.ArtifactVault.DependencyArtifact;
import org.jboss.errai.forge.constant.PomPropertyVault.Property;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;

public class GwtPluginFacet extends AbstractPluginFacet {

  public GwtPluginFacet() {
    pluginArtifact = DependencyArtifact.Gwt;
    dependencies = new ArrayList<DependencyBuilder>(0);
    executions = Arrays.asList(new Execution[] {
            ExecutionBuilder.create().addGoal("resources").addGoal("compile")
    });
    configurations = Arrays.asList(new ConfigurationElement[] {
            ConfigurationElementBuilder.create().setName("logLevel").setText("INFO"),
            ConfigurationElementBuilder.create().setName("noServer").setText("false"),
            ConfigurationElementBuilder.create().setName("server")
              .setText("org.jboss.errai.cdi.server.gwt.JBossLauncher"),
            ConfigurationElementBuilder.create().setName("disableCastChecking").setText("true"),
            ConfigurationElementBuilder.create().setName("runTarget").setText("${errai.dev.context}/index.html"),
            ConfigurationElementBuilder.create().setName("soyc").setText("false"),
            ConfigurationElementBuilder.create().setName("hostedWebApp").setText("src/main/webapp"),
            ConfigurationElementBuilder.create().setName("extraJvmArgs").setText(
                      "-Xmx712m "
                    + "-XX:CompileThreshold=7000 "
                    + "-XX:MaxPermSize=128M "
                    + "-D" + Property.JbossHome.getName() + "=" + Property.JbossHome.invoke() + " "
                    + "-D" + Property.DevContext.getName() + "=" + Property.DevContext.invoke() + " "
                    + "-Derrai.jboss.javaagent.path=${settings.localRepository}/org/jboss/errai/errai-client-local-class-hider/"
                      + Property.ErraiVersion.invoke() + "/errai-client-local-class-hider-"
                      + Property.ErraiVersion.invoke() + ".jar"
            )
    });
  }

}
