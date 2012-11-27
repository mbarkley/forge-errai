package org.jboss.errai.forge;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;

/**
 * @author pslegr
 */
public enum ErraiVersion {
    ERRAI_BUS_2_2_0_SNAPSHOT("errai-bus:2.2.0-SNAPSHOT",
            Arrays.asList(
                    DependencyBuilder.create("org.jboss.errai:errai-bus:2.2.0-SNAPSHOT"),
                    DependencyBuilder.create("org.jboss.errai:errai-ioc:2.2.0-SNAPSHOT"),
                    DependencyBuilder.create("org.jboss.errai:errai-tools:2.2.0-SNAPSHOT"),
                    DependencyBuilder.create("com.google.gwt:gwt-servlet:2.4.0"),
                    DependencyBuilder.create("com.google.gwt:gwt-user:2.4.0")
            ),
            Collections.EMPTY_LIST
    ),
    ERRAI_BUS_2_1_1_Final("errai-bus:2.1.1.Final",
            Arrays.asList(
            		DependencyBuilder.create("org.jboss.errai:errai-bus:2.1.1.Final"),
                    DependencyBuilder.create("org.jboss.errai:errai-ioc:2.1.1.Final"),
                    DependencyBuilder.create("org.jboss.errai:errai-tools:2.1.1.Final"),
                    DependencyBuilder.create("com.google.gwt:gwt-servlet:2.4.0"),
                    DependencyBuilder.create("com.google.gwt:gwt-user:2.4.0")
            ),
            Collections.EMPTY_LIST
    ),
    
    // CDI
    
    ERRAI_CDI_2_2_0_SNAPSHOT("errai-cdi:2.2.0-SNAPSHOT",
            Arrays.asList(
            	  DependencyBuilder.create("javax.servlet:servlet-api:2.5").setScopeType("provided"),
  	              DependencyBuilder.create("javax.servlet:jsp-api:2.0").setScopeType("provided"),
  	              DependencyBuilder.create("log4j:log4j:1.2.16"),
  	              DependencyBuilder.create("junit:junit:4.10").setScopeType("test"),
  	              DependencyBuilder.create("org.jboss.ejb3:jboss-ejb3-api:3.1.0").setScopeType("provided"),
  	              DependencyBuilder.create("javax.enterprise:cdi-api:1.0-SP4").setScopeType("provided"),
  	              DependencyBuilder.create("org.mvel:mvel2:2.1.Beta8"),

  	              // CDI Integration
  	              DependencyBuilder.create("org.jboss.errai:errai-cdi-client:2.2.0-SNAPSHOT"),	              
  	              DependencyBuilder.create("org.jboss.errai:errai-javax-enterprise:2.2.0-SNAPSHOT"),	              
  	              DependencyBuilder.create("org.jboss.errai:errai-weld-integration:2.2.0-SNAPSHOT"),	              
  	              DependencyBuilder.create("org.jboss.errai:errai-cdi-jetty:2.2.0-SNAPSHOT"),

  	              //Jetty & Weld
  	              DependencyBuilder.create("org.mortbay.jetty:jetty:6.1.25"),
  	              DependencyBuilder.create("org.mortbay.jetty:jetty-plus:6.1.25"),
  	              DependencyBuilder.create("org.mortbay.jetty:jetty-naming:6.1.25"),
  	              DependencyBuilder.create("org.jboss.weld.se:weld-se-core:1.1.6.Final"),
  	              DependencyBuilder.create("org.jboss.weld.servlet:weld-servlet:1.1.6.Final"),
  	              DependencyBuilder.create("org.jboss.logging:jboss-logging:3.0.0.Beta4")
            ),
            Collections.EMPTY_LIST
    ),
    ERRAI_CDI_2_1_1_Final("errai-cdi:2.1.1.Final",
            Arrays.asList(
            	  DependencyBuilder.create("javax.servlet:servlet-api:2.5").setScopeType("provided"),
  	              DependencyBuilder.create("javax.servlet:jsp-api:2.0").setScopeType("provided"),
  	              DependencyBuilder.create("log4j:log4j:1.2.16"),
  	              DependencyBuilder.create("junit:junit:4.10").setScopeType("test"),
  	              DependencyBuilder.create("org.jboss.ejb3:jboss-ejb3-api:3.1.0").setScopeType("provided"),
  	              DependencyBuilder.create("javax.enterprise:cdi-api:1.0-SP4").setScopeType("provided"),
  	              DependencyBuilder.create("org.mvel:mvel2:2.1.Beta8"),

  	              // CDI Integration
  	              DependencyBuilder.create("org.jboss.errai:errai-cdi-client:2.1.1.Final"),	              
  	              DependencyBuilder.create("org.jboss.errai:errai-javax-enterprise:2.1.1.Final"),	              
  	              DependencyBuilder.create("org.jboss.errai:errai-weld-integration:2.1.1.Final"),	              
  	              DependencyBuilder.create("org.jboss.errai:errai-cdi-jetty:2.1.1.Final"),

  	              //Jetty & Weld
  	              DependencyBuilder.create("org.mortbay.jetty:jetty:6.1.25"),
  	              DependencyBuilder.create("org.mortbay.jetty:jetty-plus:6.1.25"),
  	              DependencyBuilder.create("org.mortbay.jetty:jetty-naming:6.1.25"),
  	              DependencyBuilder.create("org.jboss.weld.se:weld-se-core:1.1.6.Final"),
  	              DependencyBuilder.create("org.jboss.weld.servlet:weld-servlet:1.1.6.Final"),
  	              DependencyBuilder.create("org.jboss.logging:jboss-logging:3.0.0.Beta4")
            ),
            Collections.EMPTY_LIST
    ),
    
    // JAX-RS
    
    ERRAI_JAXRS_2_2_0_SNAPSHOT("errai-jaxrs:2.2.0-SNAPSHOT",
            Arrays.asList(
            	  DependencyBuilder.create("org.jboss.errai:errai-common:2.2.0-SNAPSHOT"),
  	              DependencyBuilder.create("org.jboss.errai:errai-jaxrs-client:2.2.0-SNAPSHOT").setScopeType("provided"),
  	              DependencyBuilder.create("org.jboss.errai:errai-jaxrs-provider:2.2.0-SNAPSHOT"),
  	              DependencyBuilder.create("javax.enterprise:cdi-api:1.0-SP4"),
  	              DependencyBuilder.create("org.jboss.resteasy:resteasy-jaxrs:2.2.3.GA")
            ),
            Collections.EMPTY_LIST
    ),
    ERRAI_JAXRS_2_1_1_Final("errai-jaxrs:2.1.1.Final",
            Arrays.asList(
            	  DependencyBuilder.create("org.jboss.errai:errai-common:2.1.1.Final"),
  	              DependencyBuilder.create("org.jboss.errai:errai-jaxrs-client:2.1.1.Final").setScopeType("provided"),
  	              DependencyBuilder.create("org.jboss.errai:errai-jaxrs-provider:2.1.1.Final"),
  	              DependencyBuilder.create("javax.enterprise:cdi-api:1.0-SP4"),
  	              DependencyBuilder.create("org.jboss.resteasy:resteasy-jaxrs:2.2.3.GA")
            ),
            Collections.EMPTY_LIST
    ),
    
    // UI
    
    ERRAI_UI_2_2_0_SNAPSHOT("errai-ui:2.2.0-SNAPSHOT",
            Arrays.asList(
            	  DependencyBuilder.create("org.jboss.errai:errai-javaee-all:2.2.0-SNAPSHOT"),
  	              DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0:3.0.1.Final").setScopeType("provided").setPackagingType("pom")
            ),
            Collections.EMPTY_LIST
    ),
    ERRAI_UI_2_1_1_Final("errai-ui:2.1.1.Final",
            Arrays.asList(
  	              DependencyBuilder.create("org.jboss.errai:errai-javaee-all:2.1.1.Final"),
  	              DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0:3.0.1.Final").setScopeType("provided").setPackagingType("pom")
            ),
            Collections.EMPTY_LIST
    );
    
    
    

    private List<? extends Dependency> dependencies;
    private List<? extends Dependency> dependencyManagement;
    private String name;

    private ErraiVersion(String name, List<? extends Dependency> deps, List<? extends Dependency> depManagement) {
        this.name = name;
        this.dependencies = deps;
        this.dependencyManagement = depManagement;
    }

    public List<? extends Dependency> getDependencies() {
        return dependencies;
    }

    public List<? extends Dependency> getDependencyManagement() {
        return dependencyManagement;
    }

    @Override
    public String toString() {
        return name;
    }
}
