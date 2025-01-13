This is a Java project that is using Maven. 

First you need to download and install Java from https://adoptium.net/ and Maven form https://maven.apache.org/download.cgi

Then you can compile the project, package it into .jar file and install it to your local Maven repository with command:
mvn clean install

Then you can use the project as dependency in other maven projects on your computer by adding the repository and dependency to your pom.xml file:
<repositories>
     <repository>
          <id>repsy</id>
          <name>My Public Maven Repository on Repsy</name>
          <url>https://repo.repsy.io/mvn/cmolik/public</url>
     </repository>
</repositories>

<dependency>
     <groupId>tiger</groupId>
     <artifactId>tiger</artifactId>
     <version>2.0-SNAPSHOT</version>
</dependency> 

 

