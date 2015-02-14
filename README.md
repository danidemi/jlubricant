Usually you deploy a web application keeping all services well separated, you install mysql, you manage it,
you creates users and databases, then you set up a messaging system, again you manage it, create users, and so on,
then you install an application server, and finally you deploy your fancy app in it.

While there is absolutely nothing wrong with this, it is for sure tremendously slow and expensive if you just want to quickly
bring your next-fancy-webapp to the masses, and you don't have resources, people, knowledge.

![Deploy of a "Fancy Web App" the usual way](asset/deploy-the-usual-way.svg)

Wait for a minute how it would be nice if it would be possible to assemble a web application that embeds databases, messaging systems,
application server, and any other service you need, in just one executable `.jar`, that you can maybe deploy on a couple of free openshift
gears, without messing around with complex systems, yet using standard protocols that will allow you to quickly migrate to standard
architectures just when your application will need it!

![Deploy of a "Fancy Web App" the JLubricant way](asset/deploy-the-jlubricant-way.svg)

JLubricant allows you to do that.

# JLubricant

[![Build Status](https://travis-ci.org/danidemi/jlubricant.svg)](https://travis-ci.org/danidemi/jlubricant)

JLubricant allows you to quickly prototype applications providing you
* Java based services 
	* as...
		* Database engines
		* Queue systems
		* Web Servers
	* that you can quickly integrate through their common interface (`JBDC`, `JMS`, `javax.servlet`)
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