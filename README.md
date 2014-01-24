# Errai Forge Plugin

## What is this?

	This [Forge](http://forge.jboss.org/) plugin can be used to create a new Maven project with Errai, or add Errai to an existing Maven project.

## Install the plugin in Forge

First get a local copy of the plugin either using `git clone https://github.com/errai/forge-errai.git` or by downloading the [zip file](https://github.com/errai/forge-errai/archive/master.zip).

Then start forge and run the command `forge source-plugin [path to plugin]` .

## Start a new project

To start a new project with Forge, use the following command:

    new-project --named [name of project] --topLevelPackage [project package] --projectFolder [path to project]

## Add the Errai plugin to the Project

In the root directory of your new project, having installed the Errai Forge plugin, start Forge and run:

    errai setup

This will prompt you for:

* The logical name of the [GWT module](http://www.gwtproject.org/doc/latest/DevGuideOrganizingProjects.html#DevGuideModuleXml) for your project. (So if you want a module called *App* in the package `org.jboss.errai`, the logical name would be `org.jboss.errai.App` .)

* An option to give your module a different name with the "rename-to" attribute (for example, you could rename a logical module `org.jboss.errai.App` to just `app` to help shorten urls).

* The version of Errai you would like to use.

With the plugin installed, you will now be able to run GWT Development Mode with Jboss (using `mvn clean gwt:run`).

## Add Errai Features

With the plugin fully installed, you can now access it's other commands for adding, removing, and listing features of Errai.

### See the available features

To see a list of features in Errai, run

    errai list-features

To get a description of each feature, use the verbose flag:

    errai list-features -v

And to see which features you have already added, use the installed flag:

    errai list-features -i

Note that each feature has a *short name* which is how you reference this feature in other commands.

### Add or remove features

To add an errai feature to your project use the *add-feature* command:

    errai add-feature [short name]

Likewise to remove a feature, use the *remove-feature* command:

    errai remove-feature [short name]

So for example, you could try installing Errai Messaging by running

    errai add-feature messaging

Note that there is auto-complete assistance with these commands.

## Happy Coding!

Enjoy working on your new Errai project, and please report any issues on our [Jira](https://issues.jboss.org/browse/ERRAI) or on our [forum](https://community.jboss.org/en/errai). Feedback is always welcome!

