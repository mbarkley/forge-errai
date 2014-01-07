package org.jboss.errai.forge.config;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationFactory;
import org.jboss.forge.project.Project;

@Singleton
public final class ProjectConfig {

  public static enum ProjectProperty {
    MODULE_FILE(File.class),
    MODULE_LOGICAL(String.class);

    public final Class<?> valueType;

    private ProjectProperty(Class<?> type) {
      valueType = type;
    }
  }

  public static final String PREFIX = "errai-forge-";

  private final Map<ProjectProperty, Object> properties = new ConcurrentHashMap<ProjectProperty, Object>();

  private final ConfigurationFactory configFactory;

  private final Project project;

  ProjectConfig(final ConfigurationFactory factory, final Project project) {
    this.project = project;
    final Configuration config = factory.getProjectConfig(project);
    for (final ProjectProperty prop : ProjectProperty.values()) {
      String val = config.getString(getProjectAttribute(prop));
      if (val != null && !val.equals("")) {
        if (prop.valueType.equals(File.class)) {
          properties.put(prop, new File(val));
        }
        else {
          properties.put(prop, val);
        }
      }
    }
    configFactory = factory;
  }

  public <T> T getProjectProperty(ProjectProperty property, Class<T> type) {
    return type.cast(properties.get(property));
  }

  public <T> void setProjectProperty(ProjectProperty property, T value) {
    if (!property.valueType.isInstance(value)) {
      throw new IllegalArgumentException("Value for property " + property.toString() + " must be type "
              + property.valueType + ", not " + value.getClass());
    }

    final Configuration config = configFactory.getProjectConfig(project);
    properties.put(property, value);
    if (property.valueType.equals(File.class)) {
      config.setProperty(getProjectAttribute(property), File.class.cast(value).getAbsolutePath());
    }
    else {
      config.setProperty(getProjectAttribute(property), value);
    }
  }

  public static String getProjectAttribute(final ProjectProperty prop) {
    return PREFIX + prop.name();
  }

}
