package com.danidemi.jlubricant.utils.properties;

import java.util.Properties;

public class EnvProperties extends Properties {

	private static final long serialVersionUID = -7283786686695723783L;
	
	public EnvProperties(Properties defaults) {
		super(defaults);
	}
	
	public EnvProperties() {
		super();
	}

	@Override
	public String getProperty(String key) {
		
		String valueFromProperties = super.getProperty(key);
		
		if(valueFromProperties.matches("\\$\\{.+\\}")){
			String envVariable = valueFromProperties.substring("${".length(), valueFromProperties.length()-"}".length());
			String envValue = System.getenv(envVariable);
			if (envValue != null) return envValue;
		}
		
		return valueFromProperties;
		
	}
	
	@Override
	public synchronized Object get(Object key) {
		return key instanceof String ? getProperty((String)key) : super.get(key);
	}

}
