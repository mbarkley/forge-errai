    _____                    
   |  ___|__  _ __ __ _  ___ 
   | |_ / _ \| `__/ _` |/ _ \  \\
   |  _| (_) | | | (_| |  __/  //		ERRAI PLUGIN
   |_|  \___/|_|  \__, |\___| 
                   |___/      
 
Usage instructions:
=====================
1. Start Forge promt 
[no project] bin $ 

2. Install forge errai plugin or source one with "forge source-plugin [Path to errai forge plugin folder]"
Example:
[no project] bin $ forge source-plugin /home/pslegr/wfk-development/GIT-REPOS/UPSTREAM/PSLEGR-FORK/2.1.x/forge-errai

3. Create new project
new-project --named [name of project] --topLevelPackage [your project package] --projectFolder [path to your project folder]
Example:
[no project] bin $ new-project --named errai-demo --topLevelPackage org.jboss.errai --projectFolder /home/pslegr/wfk-development/FORGE/test-projects/errai-demo

4. Type "errai" to complete your command
[errai-demo] errai-demo $ errai 
bus           setup         marshaling    config        examples


Command completion help
------------------------
For easier usage we do have a built-in completion help, which will navigate you to the right commands usage
Example:
For successful usage of errai example commands you need to set up Examples Facet first with "errai example command". What will happen when you skip and want to omit this ?  

[errai-demo] errai-demo $ errai examples install-errai-bus 
Errai Example Facet is not installed. Use 'errai examples setup' to get started.

Examples
-----------
[errai-demo] errai-demo $ errai examples 
help                     setup                    install-errai-bus        install-errai-CDI        install-errai-jaxrs      install-errai-ui         
uninstall-errai-bus      uninstall-errai-CDI      uninstall-errai-jaxrs    uninstall-errai-ui

use
[errai-demo] errai-demo $ errai examples setup
to setup examples Facet.

Currently available examples to install are: errai-bus,errai-cdi,errai-ui,errai-jaxrs

Facets
---------
Note:Each of Facet introduces a different Errai module, which will be installed. By selecting one of the facets a basic project folder structure
is set up and pom.xml for project is built up.
   
[errai-demo] errai-demo $ errai bus setup-
setup-facet               setup-Errai.properties    setup-log4j.properties

[errai-demo] errai-demo $ errai bus setup-facet 

 ? Facet [forge.maven.WebResourceFacet] requires packaging type(s) [war], but is currently [jar]. Update packaging? (Note: this could deactivate other plugins in your project.) [Y/n] Y
***SUCCESS*** Installed [forge.maven.WebResourceFacet] successfully.
***SUCCESS*** Installed [ErraiBusFacet] successfully.    


Properties
------------
[errai-demo] errai-demo $ errai bus setup-Errai.properties 
***SUCCESS*** ErraiApp.properties file has been installed.
Wrote /home/pslegr/wfk-development/FORGE/test-projects/errai-demo/src/main/resources/ErraiApp.properties


errai bus Facet
-----------------
[errai-demo] errai-demo $ errai bus 
setup-facet                              setup-Errai.properties                   setup-log4j.properties                   
rpc-generate-empty-service-impl          rpc-generate-simple-service-impl         rpc-generate-remote-from-service         
rpc-generate-remotes-for-all-services    rpc-invoke-endpoint

Generation of emty service *Impl class
[errai-demo] errai-demo $ errai bus rpc-generate-empty-service-impl
Wrote /home/pslegr/wfk-development/FORGE/test-projects/errai-demo/src/main/java/org/jboss/errai/YourEmptyServiceImpl.java

Generation of simple service *Impl class
[errai-demo] errai-demo $ errai bus rpc-generate-simple-service-impl 
Wrote /home/pslegr/wfk-development/FORGE/test-projects/errai-demo/src/main/java/org/jboss/errai/SimpleServiceImpl.java

...

package org.jboss.errai;

import java.util.Date;

import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.server.annotations.Service;

@Service
public class SimpleServiceImpl implements SimpleRemoteService
{
   public boolean simpleService()
   {
      return true;
   }
}

...

Generation of @remote from simple service

[errai-demo] errai-demo $ errai bus rpc-generate-remote-from-service --from org.jboss.errai.SimpleServiceImpl
from: org.jboss.errai.SimpleServiceImpl
sourceFilePath: /home/pslegr/wfk-development/FORGE/test-projects/errai-demo/src/main/java/org/jboss/errai/SimpleServiceImpl.java
Wrote /home/pslegr/wfk-development/FORGE/test-projects/errai-demo/src/main/java/org/jboss/errai/SimpleRemoteService.java

...

package org.jboss.errai;

import java.util.Date
import org.jboss.errai.bus.client.api.Message
import org.jboss.errai.bus.client.api.MessageCallback
import org.jboss.errai.bus.client.api.base.MessageBuilder
import org.jboss.errai.bus.server.annotations.Service

@Remote
public interface SimpleRemoteService {
    public boolean simpleService ()
}

...

Have fun !
 