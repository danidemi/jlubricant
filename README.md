# JLubricant
Java Power Ups!

[![Build Status](https://travis-ci.org/danidemi/jlubricant.svg)](https://travis-ci.org/danidemi/jlubricant)

# Big Picture
JLubricant has the goal to allows you to quickly prototype applications providing you
* Java based services 
	* as...
		* Database engines
		* Queue systems
		* Web Servers
		* (more to come)
	* that you can quickly integrate through their common interface.
* Utilities that avoid you to cope with annoying and repetitive tasks.

# Use JLubricant

JLubricant modules are available as a Maven dependecies

	<dependency>
	    <groupId>com.danidemi.jlubricant</groupId>
	    <artifactId>jlubricant-utils</artifactId>
	    <version>0.0.14</version>
	</dependency>

	<dependency>
	    <groupId>com.danidemi.jlubricant</groupId>
	    <artifactId>jlubricant-embeddable-hsql</artifactId>
	    <version>0.0.14</version>
	</dependency>
	
	<dependency>
	    <groupId>com.danidemi.jlubricant</groupId>
	    <artifactId>jlubricant-slf4j</artifactId>
	    <version>0.0.14</version>
	</dependency>

# Build

## Library

Build JLubricant from sources in three simple steps. 

	$ git clone https://github.com/danidemi/jlubricant.git
	$ cd jlubricant
	$ mvn clean install
	
## Documentation

To generate and check the documentation

	$ mvn site:site site:stage
	$ firefox target/staging/index.html &
	
	
# Contribution

Please, feel free to contribute with ideas, code, questions, submitting issues, samples.