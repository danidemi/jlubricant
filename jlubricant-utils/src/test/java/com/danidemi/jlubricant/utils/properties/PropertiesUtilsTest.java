package com.danidemi.jlubricant.utils.properties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class PropertiesUtilsTest {

	private Properties defaults;
	private Properties values;

	@Before
	public void setUpValues(){
		
		defaults = new Properties();
		defaults.setProperty("title", "WaterXhip Down");
		defaults.setProperty("author", "Richard Adams");
		
		values = new Properties(defaults);
		values.setProperty("title", "Watership Down");
		
	}
	
	@Test
	public void propertiesShouldLookIntoDefaults() {
				
		// when
		String title = values.getProperty("title");
		String author = values.getProperty("author");
		
		// then
		assertThat( title, equalTo("Watership Down"));
		assertThat( author, equalTo("Richard Adams"));
		
	}
	
	@Test
	public void putAllShouldNotConsiderDefaults() {
		
		// given
		Properties all = new Properties();
		
		// when
		all.putAll( values );
		String author = all.getProperty("author");
		
		// then
		assertThat( author, nullValue());
		
	}
	
	@Test
	public void shouldFlattenPropertiesWithDefaults() {
		
		// given
		Properties all = new Properties();
		
		// when
		all.putAll( PropertiesUtils.flatten( values ) );
		String author = all.getProperty("author");
		String title = all.getProperty("title");
		
		// then
		assertThat( title, equalTo("Watership Down"));
		assertThat( author, equalTo("Richard Adams"));
		
	}	

}
