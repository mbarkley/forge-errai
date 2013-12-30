package org.jboss.errai.forge.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class ProjectConfig {

  public static enum ProjectProperty {
    MODULE(File.class);

    public final Class<?> valueType;

    private ProjectProperty(Class<?> type) {
      valueType = type;
    }
  }

  // TODO populate this configuration
  private static ProjectConfig mainConfig = new ProjectConfig();

  private final Map<ProjectProperty, Object> properties = new HashMap<ProjectProperty, Object>();
  
  public static ProjectConfig getMainConfig() {
    return mainConfig;
  }

  public <T> T getProjectProperty(ProjectProperty property, Class<T> type) {
    return type.cast(properties.get(property));
  }

  public <T> void setProjectProperty(ProjectProperty property, T value) {
    if (property.valueType.isAssignableFrom(value.getClass())) {
      throw new IllegalArgumentException("Value for property " + property.toString() + " must be type "
              + property.valueType + ", not " + value.getClass());
    }
    
    properties.put(property, value);
  }

}
