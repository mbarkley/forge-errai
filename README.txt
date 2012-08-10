    _____                    
   |  ___|__  _ __ __ _  ___ 
   | |_ / _ \| `__/ _` |/ _ \  \\
   |  _| (_) | | | (_| |  __/  //		ERRAI PLUGIN
   |_|  \___/|_|  \__, |\___| 
                   |___/      
 
Usage instructions:
-----------------------
1. Start Forge promt 
[no project] bin $ 

2. Install forge errai plugin or source one with "forge source-plugin [Path to errai forge plugin folder]"
Example:
[no project] bin $ forge source-plugin /home/pslegr/wfk-development/GIT-REPOS/UPSTREAM/PSLEGR-FORK/2.1.x/forge-errai

3. Create new project
new-project --named [name of project] --topLevelPackage [your project package] --projectFolder [path to your project folder]
Example:
[no project] bin $ new-project --named errai-bus --topLevelPackage org.jboss.errai --projectFolder /home/pslegr/wfk-development/FORGE/test-projects/errai-bus

4. Run the "errai setup" command to install the ErraiFacets.
You can choose from currenly available 4 Errai Facets
Example:
[errai-bus] errai-bus $ errai setup 
Which Errai module to install?

  1 - [Errai Bus Facet]*
  2 - [Errai CDI Facet]
  3 - [Errai Jaxrs Facet]
  4 - [Errai UI Facet]

 ? Choose an option by typing the number of the selection [*-default]  [0] 

Note:Each of Facet introduces a different Errai module, which will be installed. By selecting one of the facets a basic project folder structure
is set up and pom.xml for project is built up.   

5. Install one the available demo projects.
You can find out, what demo projects are available by typing an help command 
[errai-bus] errai-bus $ errai help
Use the install commands to install:
install-errai-bus: an example of simple Errai application
install-errai-cdi: an example of Errai CDI-based application
install-errai-jaxrs: an example of Errai Jaxrs applicatiton
install-errai-ui: an example of Errai UI applicatiton
uninstall-errai-bus: uninstall simple Errai Bus application
uninstall-errai-cdi: uninstal Errai CDI-based application
uninstall-errai-jaxrs: unistall Errai Jaxrs application
uninstall-errai-ui: unistall Errai UI application

Note: Once you selected a specific Facet for example [Errai CDI Facet] you will be able to either install/uninstall only CDI demo, when
you wish to set up a different project example simply create new project (from step 2.) or install a different Errai Facet.

Have fun !
 