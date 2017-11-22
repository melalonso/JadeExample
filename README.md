# JadeExample
Example of Java Agent Development Framework for the course of Distributed Systems of the University of Costa Rica.

### How to run
It is necessary to include the dependency of JADE. Could be in form of a jar file or the maven dependency.

In the case of a jar file just download JADE's binaries from http://jade.tilab.com/download/jade/license/jade-download/, unzip and include the jar in the project.

If want to include it as a maven dependency them add the following lines to your pom file:

```
<repository> 
    <id>tilab</id> 
    <url>http://jade.tilab.com/maven/</url> 
</repository>

<dependency>  
    <groupId>com.tilab.jade</groupId> 
    <artifactId>jade</artifactId> 
    <version>4.5.0</version>  
</dependency>
```


After that you need to modify the project's run configuration to set the main class to `jade.Boot` and pass the argument `-gui`.


Then you can run the project and the JADE Remote Agent Management GUI will show up.