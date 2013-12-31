package org.jboss.errai.forge.config;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationFactory;

@Singleton
public final class ProjectConfig {

  public static enum ProjectProperty {
    MODULE(File.class);

    public final Class<?> valueType;

    private ProjectProperty(Class<?> type) {
      valueType = type;
    }
  }

  public static final String PREFIX = "errai-forge-";

  private final Map<ProjectProperty, Object> properties = new ConcurrentHashMap<ProjectProperty, Object>();

  private final ConfigurationFactory configFactory;

  @Inject
  public ProjectConfig(final ConfigurationFactory factory) {
    final Configuration config = factory.getUserConfig();
    for (final ProjectProperty prop : ProjectProperty.values()) {
      Object val = config.getProperty(getProjectAttribute(prop));
      if (val != null) {
        properties.put(prop, val);
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

    final Configuration config = configFactory.getUserConfig();
    properties.put(property, value);
    config.setProperty(getProjectAttribute(property), value);
  }
  
  public static String getProjectAttribute(final ProjectProperty prop) {
    return PREFIX + prop.name();
  }

}
