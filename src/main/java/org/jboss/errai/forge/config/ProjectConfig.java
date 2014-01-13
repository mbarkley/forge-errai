package org.jboss.errai.forge.config;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationFactory;
import org.jboss.forge.project.Project;

/**
 * A singleton class for accessing project-wide plugin settings.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Singleton
public final class ProjectConfig {

  /**
   * An enumeration of project properties stored in a {@link ProjectConfig}.
   * 
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static enum ProjectProperty {
    MODULE_FILE(File.class), MODULE_LOGICAL(String.class), ERRAI_VERSION(String.class);

    /**
     * The type of value stored by this property.
     */
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

  /**
   * Get the value of a {@link ProjectProperty}.
   * 
   * @param property
   *          The {@link ProjectProperty} to which the returned value belongs.
   * @param type
   *          The type of the returned value.
   * @return The value associated with the given {@link ProjectProperty}, or
   *         null if none exists.
   */
  public <T> T getProjectProperty(ProjectProperty property, Class<T> type) {
    return type.cast(properties.get(property));
  }

  /**
   * Set the value of a {@link ProjectProperty}. This value will persist in the
   * forge configurations.
   * 
   * @param property
   *          The {@link ProjectProperty} to which a value will be assigned.
   * @param value
   *          The value to assign to the property.
   * @throws IllegalArgumentException
   *           If the class of the {@code value} does not match
   *           {@code property.valueType}.
   */
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

  /**
   * @return The name used to store and retrieve persistent project configurations.
   */
  public static String getProjectAttribute(final ProjectProperty prop) {
    return PREFIX + prop.name();
  }

}
