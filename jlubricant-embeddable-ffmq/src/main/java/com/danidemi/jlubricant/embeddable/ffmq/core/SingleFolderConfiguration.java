package com.danidemi.jlubricant.embeddable.ffmq.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.timewalker.ffmq3.FFMQCoreSettings;
import net.timewalker.ffmq3.utils.Settings;

public class SingleFolderConfiguration {

	private File baseDirectory;
	private Properties usedProps;
	
	private static final Logger log = LoggerFactory.getLogger(SingleFolderConfiguration.class);
	
	public SingleFolderConfiguration(File base) {
		this.baseDirectory = base;
		File destinationDefinitionsDirectory = new File(baseDirectory, "destination_definition");
		File templatesDirectory = new File(baseDirectory, "templates");
		File defaultDataDirectory = new File(baseDirectory, "data");
		
		List<File> directories = new ArrayList<File>();
		directories.add(destinationDefinitionsDirectory);
		directories.add(templatesDirectory);
		directories.add(defaultDataDirectory);
		
		for (File dir : directories) {
			dir.mkdirs();
			log.info("Creating {}", dir.getAbsolutePath());
		}
		
		usedProps = new Properties();
		usedProps.setProperty(FFMQCoreSettings.DESTINATION_DEFINITIONS_DIR, destinationDefinitionsDirectory.getAbsolutePath());
		usedProps.setProperty(FFMQCoreSettings.TEMPLATES_DIR, templatesDirectory.getAbsolutePath());
		//usedProps.setProperty(FFMQCoreSettings.TEMPLATE_MAPPING_FILE, templateMapping.getAbsolutePath());
		usedProps.setProperty(FFMQCoreSettings.DEFAULT_DATA_DIR, defaultDataDirectory.getAbsolutePath());
		
		log.info("Properties: {}", usedProps);
	}
	
	public Properties getProperties() {
		return usedProps;
	}
	
	public Settings asSettings(){
		return new Settings(getProperties());
	}
	
}
