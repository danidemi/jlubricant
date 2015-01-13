package com.danidemi.jlubricant.utils.properties;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		
		Pattern pattern = Pattern.compile("\\$\\{.+?\\}");
		Matcher matcher = pattern.matcher(valueFromProperties);
		
		StringBuilder sb = new StringBuilder();
		
		int index = 0;
		while (matcher.find()) {
			int s = matcher.start();
		    int e = matcher.end();
		    String envVariable = matcher.group();
		    		    
		    String envValue = System.getenv(envVariable.substring(2, envVariable.length()-1));
		    if(envValue != null){
		    	
		    	if(index == 0){
		    		sb.append( valueFromProperties.substring(0, s) );
		    	}
		    	
		    	sb.append( envValue );
		    	
		    }else{
		    	
		    	sb.append(envVariable);
		    	
		    }
		    
		    index = e;
		    
		    
		    
		}		
		
		if(index!=valueFromProperties.length()){
			sb.append( valueFromProperties.substring(index) );
		}
				
		return sb.toString();
		
	}
	
	@Override
	public synchronized Object get(Object key) {
		return key instanceof String ? getProperty((String)key) : super.get(key);
	}

}
