# Clans
A social life simulator.

Clans is a "life" simulator written in Java, based on the Drools[1] rule engine and using JFX[2] for the graphical user interface.

The aim of Clans is to experiment with a rule engine to achieve some sort of "artificial intelligence". 

Current elements of Clans are:
- Individuals: the main characters, individuals live in a world also populated by plants. Individuals are governed by basic instincts, like hunger and the impulse to mate and generate children. They are DNA-based, with characteristics that are passed down to the progeny, make experiences and can learn from their mistakes.
- Plants: plants represent food and the main source of energy for individuals. They can also represent a threat, in the case of poisonous plants.

The FXGL[3] game development framework is used to simplify JFX development.
Also the TilesFX[7] library for dashboard elements is used to provide gauges on what's going on inside the Clans world.

## Build environment setup
The Clans project can be easily developed in the Eclipse IDE[6] and built with Maven[4].

Follow these steps to import the project into Eclipse, build and run the application.

Prerequisites:
- Java 13 JDK[5]
- Eclipse IDE for Enterprise Java developers[6]
- JFX 13 SDK[2]

Steps:
1. Download Java 13 JDK[5] (the JDK, not the JRE).
1. Install the package, or unzip it and add the ../bin directory to your PATH.
1. Download Eclipse[6]
1. Unzip the package to some directory
1. Edit the eclipse.ini file found on the root forder of the extracted package and add the following line at the top:
   - On Windows:
   ```
   -vm
   <path-to-your-jdk-13>/bin/javaw.exe
   ```
   - On Linux:
   ```
   -vm
   <path-to-your-jdk-13>/bin/java
   ```
1. Download the JFX 13 SDK[2]
1. Extract the package into some directory.
1. Launch Eclipse
1. Define a User Library with the JFX JARs in it:
   1. In Eclipse, go to Windows -> Settings -> Java -> Build Path -> User Libraries
   1. Click "New..." a,d give the library a name such as "JFX13", then "OK"
   1. With the library selected in the content pane, click "Add external JARs..."
   1. Navigate to the folder where you have extracted the JFX SDK and to the "lib" sub-folder.
   1. Select all the JARs there contained (not the sub-folders), then click "Open".
1. Clone this git repository[8] and import the "clans" project into Eclipse.
1. Update the project with the Maven dependencies:
   1. In the Project Explorer Eclipse view, right-click on the Clans project (root node), then Maven -> Update project.
   1. Click "OK".
1. Build the project using Maven:
   1. In the Project Explorer Eclipse view, right-click on the "pom.xml" file, then "Run as" -> "Maven install". 
1. Configure the "Clans" application to use the JFX User Library at runtime:
   1. In the Project Explorer Eclipse view, right-click on the Clans class, then "Run as" -> "Run configurations"
   1. On the left pane, double-click on "Java application"
   1. On the right pane, navigate to the "Dependencies" tab
   1. From the "Add modules" drop-down list at the bottom, select "ALL-MODULE-PATH"
   1. Click on "Apply and run"

## References
*[1]: Drools: https://www.drools.org
*[2]: JFX: https://openjfx.io
*[3]: FXGL: https://github.com/AlmasB/FXGL
*[4]: Maven: https://maven.apache.org
*[5]: Java 13: https://www.oracle.com/technetwork/java/javase/downloads/jdk13-downloads-5672538.html
*[6]: Eclipse IDE for Enterprise Java Developers: https://www.eclipse.org/downloads/packages/
*[7]: TilesFx: https://github.com/HanSolo/tilesfx
*[8]: On how to clone a Github repository and add it to Eclipse, for example follow [this tutorial](https://github.com/collab-uniba/socialcde4eclipse/wiki/How-to-import-a-GitHub-project-into-Eclipse)
