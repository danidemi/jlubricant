package com.danidemi.jlubricant.utils.properties;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.Assume;
import org.junit.Test;

public class EnvPropertiesTest {

	@Test
	public void shouldReplaceEnvironmentVariables() {
		
		// given
		Map<String, String> getenv = System.getenv();
		Assume.assumeThat("This system does not have env vars that can be used for testing.", getenv.size(), greaterThan(0));
		
		Entry<String, String> envVar = getenv.entrySet().iterator().next();
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

}
