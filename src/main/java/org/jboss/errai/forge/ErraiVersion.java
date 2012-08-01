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

    ERRAI_2_1_0("Errai 2.1-SNAPSHOT",
            Arrays.asList(
                    DependencyBuilder.create("org.jboss.errai:errai-common:2.1-SNAPSHOT"),
                    DependencyBuilder.create("org.jboss.errai:errai-bus:2.1-SNAPSHOT"),
                    DependencyBuilder.create("org.jboss.errai:errai-ioc:2.1-SNAPSHOT"),
                    DependencyBuilder.create("org.jboss.errai:errai-tools:2.1-SNAPSHOT"),
                    DependencyBuilder.create("com.google.gwt:gwt-servlet:2.4.0"),
                    DependencyBuilder.create("com.google.gwt:gwt-user:2.4.0")
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
