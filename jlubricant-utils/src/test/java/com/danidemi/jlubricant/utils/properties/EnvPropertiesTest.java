package com.danidemi.jlubricant.utils.properties;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.Assume;
import org.junit.Test;

public class EnvPropertiesTest {
	
	@Test
	public void shouldReplaceValueStartingWithProperty() {
		
		// given
		Entry<String, String> envVar = findAnEnvironmentVariable();
		Assume.assumeThat("This system does not have env vars that can be used for testing.", envVar, notNullValue());
		
		String envVariable = envVar.getKey();
		String envValue = envVar.getValue();
		
		Properties properties = new EnvProperties();
		properties.setProperty("aVariable", format("${%1$s}======", envVariable));
		
		// when
		String propertyValue = properties.getProperty("aVariable");
		Object getValue = properties.get("aVariable");
		
		// then
		assertThat("The replacement didn't take place.", propertyValue, equalTo(envValue + "======") );
		assertThat("The replacement didn't take place.", getValue, equalTo((Object)(envValue + "======")) );
		
	}
	
	@Test
	public void shouldReplaceValueEndingWithProperty() {
		
		// given
		Entry<String, String> envVar = findAnEnvironmentVariable();
		Assume.assumeThat("This system does not have env vars that can be used for testing.", envVar, notNullValue());
		
		String envVariable = envVar.getKey();
		String envValue = envVar.getValue();
		
		Properties properties = new EnvProperties();
		properties.setProperty("aVariable", format("=========${%1$s}", envVariable));
		
		// when
		String propertyValue = properties.getProperty("aVariable");
		Object getValue = properties.get("aVariable");
		
		// then
		assertThat("The replacement didn't take place.", propertyValue, equalTo("=========" + envValue) );
		assertThat("The replacement didn't take place.", getValue, equalTo((Object)("=========" + envValue)) );
		
	}		
	
	@Test
	public void shouldReplaceTwoEnvironmentVariableSpecifiedAsAValue() {
		
		// given
		Entry<String, String> envVar = findAnEnvironmentVariable();
		Assume.assumeThat("This system does not have env vars that can be used for testing.", envVar, notNullValue());
		
		String envVariable = envVar.getKey();
		String envValue = envVar.getValue();
		
		Properties properties = new EnvProperties();
		properties.setProperty("aVariable", format("double:${%1$s}${%1$s}", envVariable));
		
		// when
		String propertyValue = properties.getProperty("aVariable");
		Object getValue = properties.get("aVariable");
		
		// then
		assertThat("The replacement didn't take place.", propertyValue, equalTo("double:" + envValue + envValue) );
		assertThat("The replacement didn't take place.", getValue, equalTo((Object)("double:" + envValue + envValue)) );
		
	}	

	@Test
	public void shouldReplaceASingleEnvironmentVariableSpecifiedAsAValue() {
		
		// given
		Entry<String, String> envVar = findAnEnvironmentVariable();
		Assume.assumeThat("This system does not have env vars that can be used for testing.", envVar, notNullValue());
		
		String envVariable = envVar.getKey();
		String envValue = envVar.getValue();
		
		Properties properties = new EnvProperties();
		properties.setProperty("aVariable", format("${%s}", envVariable));
		
		// when
		String propertyValue = properties.getProperty("aVariable");
		Object getValue = properties.get("aVariable");
		
		// then
		assertThat("The replacement didn't take place.", propertyValue, equalTo(envValue) );
		assertThat("The replacement didn't take place.", getValue, equalTo((Object)envValue) );
		
	}

	private Entry<String, String> findAnEnvironmentVariable() {
		Map<String, String> getenv = System.getenv();
		Iterator<Entry<String, String>> iterator = getenv.entrySet().iterator();
		Entry<String, String> envVar = null;
		do {
			envVar = iterator.next();
		}while(envVar == null && iterator.hasNext());
		return envVar;
	}

}
