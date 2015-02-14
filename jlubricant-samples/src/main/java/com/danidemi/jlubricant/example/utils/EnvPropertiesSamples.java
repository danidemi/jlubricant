package com.danidemi.jlubricant.example.utils;

import java.util.Properties;

import com.danidemi.jlubricant.utils.properties.EnvProperties;

public class EnvPropertiesSamples {
	
	public static void main(String[] args) {
	
		Properties envProperties = new EnvProperties();
		envProperties.setProperty("pathdescription", "the path is ${PATH}");
		System.out.println( envProperties.getProperty("pathdescription") );
		
	}
	
}
