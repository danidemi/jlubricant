package com.danidemi.jlubricant.example.utils;

import java.util.Properties;

import org.junit.Test;

import com.danidemi.jlubricant.utils.properties.EnvProperties;

public class EnvPropertiesSamples {
		
	@Test public void howYouCanReplacePath(){
		Properties envProperties = new EnvProperties();
		envProperties.setProperty("pathdescription", "the path is ${PATH}");
		System.out.println( envProperties.getProperty("pathdescription") );		
	}
	
}
