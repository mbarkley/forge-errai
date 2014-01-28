package org.jboss.errai.forge.facet.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.errai.forge.config.ProjectConfig;
import org.jboss.errai.forge.config.ProjectConfig.ProjectProperty;
import org.jboss.errai.forge.constant.ModuleVault;
import org.jboss.errai.forge.constant.ModuleVault.Module;
import org.jboss.errai.forge.test.base.ForgeTest;
import org.jboss.forge.addon.facets.Faceted;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.junit.Test;

public class ModuleFacetTest extends ForgeTest {

  public static class SimpleModuleFacet extends AbstractModuleFacet {
    public SimpleModuleFacet() {
      modules = Arrays.asList(new ModuleVault.Module[] { Module.GwtUser, Module.ErraiCommon });
    }
  }

  @Test
  public void testEmptyModuleInstall(ProjectConfig config, SimpleModuleFacet facet) throws Exception {
    final Project project = initializeJavaProject();
    final File moduleFile = makeBlankModuleFile(project, ModuleCoreFacet.emptyModuleContents);
    config.setProjectProperty(ProjectProperty.MODULE_FILE, moduleFile);

    facetFactory.install((Faceted<ProjectFacet>) project, SimpleModuleFacet.class);

    final String moduleContent = getFileContentAsString(moduleFile);
    assertTrue(moduleContent, moduleContent.contains("<inherits name=\"org.jboss.errai.common.ErraiCommon\"/>"));
    assertTrue(moduleContent, moduleContent.contains("<inherits name=\"com.google.gwt.user.User\"/>"));
    assertEquals(2, countMatches("<inherits name=\"[^\"]*\"/>", moduleContent));
  }

  @Test
  public void testNonEmptyModuleInstall(ProjectConfig config, SimpleModuleFacet facet) throws Exception {
    final Project project = initializeJavaProject();
    final String body = ModuleCoreFacet.emptyModuleContents.replace("</module>",
            "<inherits name=\"org.jboss.errai.common.Logging\"/></module>");
    final File moduleFile = makeBlankModuleFile(project, body);
    config.setProjectProperty(ProjectProperty.MODULE_FILE, moduleFile);

    facetFactory.install((Faceted<ProjectFacet>) project, SimpleModuleFacet.class);

    final String moduleContent = getFileContentAsString(moduleFile);
    assertTrue(moduleContent, moduleContent.contains("<inherits name=\"org.jboss.errai.common.Logging\"/>"));
    assertTrue(moduleContent, moduleContent.contains("<inherits name=\"org.jboss.errai.common.ErraiCommon\"/>"));
    assertTrue(moduleContent, moduleContent.contains("<inherits name=\"com.google.gwt.user.User\"/>"));
    assertEquals(3, countMatches("<inherits name=\"[^\"]*\"/>", moduleContent));
  }

  @Test
  public void testModuleCoreFacetWithModule(ProjectConfig config, ModuleCoreFacet facet) throws Exception {
    final Project project = initializeJavaProject();
    final File moduleFile = makeBlankModuleFile(project, ModuleCoreFacet.emptyModuleContents);
    config.setProjectProperty(ProjectProperty.MODULE_FILE, moduleFile);

    facetFactory.install((Faceted<ProjectFacet>) project, ModuleCoreFacet.class);

    final String moduleContent = getFileContentAsString(moduleFile);
    assertTrue(moduleContent, moduleContent.contains("<inherits name=\"com.google.gwt.user.User\"/>"));
    assertEquals(1, countMatches("<inherits name=\"[^\"]*\"/>", moduleContent));
  }

  @Test
  public void testModuleCoreFacetWithoutModule(ProjectConfig config, ModuleCoreFacet facet) throws Exception {
    final Project project = initializeJavaProject();
    final File moduleFile = makeBlankModuleFile(project, ModuleCoreFacet.emptyModuleContents);
    moduleFile.delete();
    config.setProjectProperty(ProjectProperty.MODULE_FILE, moduleFile);

    facetFactory.install((Faceted<ProjectFacet>) project, ModuleCoreFacet.class);

    final String moduleContent = getFileContentAsString(moduleFile);
    assertTrue(moduleContent, moduleContent.contains("<inherits name=\"com.google.gwt.user.User\"/>"));
    assertEquals(1, countMatches("<inherits name=\"[^\"]*\"/>", moduleContent));
  }

  @Test
  public void testAbstractModuleFacetIsInstalled(ProjectConfig config, SimpleModuleFacet facet) throws Exception {
    final Project project = initializeJavaProject();
    String body = ModuleCoreFacet.emptyModuleContents.replace("</module>",
            "<inherits name='org.jboss.errai.common.ErraiCommon'/>\n" + "<inherits name='com.google.gwt.user.User'/>\n"
                    + "</module>");
    final File moduleFile = makeBlankModuleFile(project, body);
    config.setProjectProperty(ProjectProperty.MODULE_FILE, moduleFile);
    facet.setFaceted(project);

    assertTrue(facet.isInstalled());
  }

  @Test
  public void testAbstractModuleFacetIsInstalledNegative(ProjectConfig config, SimpleModuleFacet facet)
          throws Exception {
    final Project project = initializeJavaProject();
    String body = ModuleCoreFacet.emptyModuleContents;
    final File moduleFile = makeBlankModuleFile(project, body);
    config.setProjectProperty(ProjectProperty.MODULE_FILE, moduleFile);
    facet.setFaceted(project);

    assertFalse(facet.isInstalled());
  }

  @Test
  public void testAbstractModuleFacetUninstall(ProjectConfig config, SimpleModuleFacet facet) throws Exception {
    final Project project = initializeJavaProject();
    String body = ModuleCoreFacet.emptyModuleContents.replace("</module>",
            "<inherits name='org.jboss.errai.common.ErraiCommon'/>\n" + "<inherits name='com.google.gwt.user.User'/>\n"
                    + "</module>");
    final File moduleFile = makeBlankModuleFile(project, body);
    config.setProjectProperty(ProjectProperty.MODULE_FILE, moduleFile);
    facet.setFaceted(project);

    boolean res = facet.uninstall();

    assertTrue(res);
    assertEquals(0, countMatches("<inherits name=\"[^\"]*\"/>", getFileContentAsString(moduleFile)));
  }

  private int countMatches(final String regex, final String content) {
    final Pattern pattern = Pattern.compile(regex);
    final Matcher matcher = pattern.matcher(content);

    int count = 0;
    while (matcher.find())
      count++;

    return count;
  }

  private File makeBlankModuleFile(final Project project, final String body) throws IOException {
    final File moduleFile = new File(project.getFacet(MavenFacet.class).getPOM().getBuild()
            .getSourceDirectory(), "org/jboss/errai/Test.gwt.xml");
    moduleFile.getParentFile().mkdirs();
    moduleFile.createNewFile();
    final FileWriter writer = new FileWriter(moduleFile);
    writer.append(body);
    writer.close();

    return moduleFile;
  }

  private String getFileContentAsString(File f) throws IOException {
    final StringBuilder builder = new StringBuilder();
    final FileReader reader = new FileReader(f);
    char[] buf = new char[256];
    int amt;
    do {
      amt = reader.read(buf);
      if (amt > -1)
        builder.append(buf, 0, amt);
      else
        break;
    }
    while (true);
    reader.close();

    return builder.toString();
  }

}
