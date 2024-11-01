This is a Java project that is using Maven. 

First you need to download and install Java from https://adoptium.net/ and Maven form https://maven.apache.org/download.cgi

Then you can compile the project, package it into .jar file and install it to your local Maven repository with command:
mvn clean install

Then you can use the project as dependency in other maven projects on your computer by adding dependency to your pom.xml file:
<dependency>
     <groupId>tiger</groupId>
     <artifactId>tiger</artifactId>
     <version>2.0-SNAPSHOT</version>
</dependency> 

 

