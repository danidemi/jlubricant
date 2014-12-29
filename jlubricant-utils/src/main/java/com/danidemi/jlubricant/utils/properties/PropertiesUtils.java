package com.danidemi.jlubricant.utils.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtils {
	
	private PropertiesUtils(){
		throw new UnsupportedOperationException("Not intended to be instantiated.");
	}

	/**
	 * Return a new {@link Properties} without defaults in which all entries are defined in the returned one.
	 * So for instance if you have such Properties...
	 * <pre>
	 * 	defaults = new Properties();
	 *  defaults.setProperty("title", "WaterXhip Down");
	 *  defaults.setProperty("author", "Richard Adams");
	 *  values = new Properties(defaults);
	 *  values.setProperty("title", "Watership Down");
	 * </pre>
	 * ...flatten(values) returns a Properties in which both "title" and "author" keys are defined.
	 */
	public static Properties flatten(Properties values) {
		
		Properties result = new Properties();
		
		Object key = null;
		
		Set<String> propertyNames = values.stringPropertyNames();
		for (String propertyName : propertyNames) {
			result.setProperty(propertyName, values.getProperty(propertyName));
		}
		return result;
	}

	public static void storeToFile(Properties props, File file) throws FileNotFoundException, IOException {
		props.store(new FileOutputStream(file), null);
	}
	
	public static void storeToFile(Properties props, File file, String comments) throws FileNotFoundException, IOException {
		props.store(new FileOutputStream(file), comments);
	}
	
	public static void storeToXmlFile(Properties props, File file, String comments) throws FileNotFoundException, IOException {
		props.storeToXML(new FileOutputStream(file), comments);
	}
	
	public static void storeToXmlFile(Properties props, File file) throws FileNotFoundException, IOException {
		props.storeToXML(new FileOutputStream(file), null);
	}			

}
