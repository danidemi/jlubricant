package com.danidemi.jlubricant.embeddable.ffmq.core;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.danidemi.jlubricant.embeddable.EmbeddableServer;
import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.utils.properties.PropertiesUtils;

import net.timewalker.ffmq3.FFMQCoreSettings;
import net.timewalker.ffmq3.FFMQServer;
import net.timewalker.ffmq3.utils.Settings;

public class FileBasedFFMQEmbeddableServer implements EmbeddableServer {
	
	private FFMQServer server;
	private boolean started;
	
	public FileBasedFFMQEmbeddableServer() {
		server = null;
		started = false;
	}

	public void start() throws ServerException {
		
		try{
			
			File baseDirectory = new File( FileUtils.getTempDirectory(), "ffmq" );
			baseDirectory.mkdirs();
			
			File destinationDefinitionsDirectory = new File(baseDirectory, "destination_definition");
			File templatesDirectory = new File(baseDirectory, "templates");
			File defaultDataDirectory = new File(baseDirectory, "data");
			
			File templateMapping = new File(baseDirectory, "templates.mappings");
			FileUtils.copyInputStreamToFile(FFMQServer.class.getResourceAsStream("/templates.mapping"), templateMapping);
			
			com.danidemi.jlubricant.utils.file.FileUtils.mkdirs(destinationDefinitionsDirectory, templatesDirectory, defaultDataDirectory);
			
			Properties adminQueue = new Properties();
			adminQueue.setProperty("name", "ADMIN_QUEUE_TEMPLATE");
			adminQueue.setProperty("persistentStore.initialBlockCount", "0");
			adminQueue.setProperty("memoryStore.maxMessages", "1000");
			PropertiesUtils.storeToFile( adminQueue, new File(templatesDirectory, "queue-" + "ADMIN_QUEUE_TEMPLATE" + ".properties") );
			
			Properties queue = new Properties();
			queue.setProperty("name","DEFAULT_QUEUE_TEMPLATE");
			queue.setProperty("persistentStore.initialBlockCount","1000");
			queue.setProperty("persistentStore.autoExtendAmount","1000");
			queue.setProperty("persistentStore.maxBlockCount","10000");
			queue.setProperty("persistentStore.blockSize","4096");
			queue.setProperty("persistentStore.dataFolder",defaultDataDirectory.getAbsolutePath());
			queue.setProperty("persistentStore.useJournal","true");
			queue.setProperty("persistentStore.syncMethod","2");
			queue.setProperty("persistentStore.journal.preAllocateFiles","false");
			queue.setProperty("memoryStore.maxMessages","1000");
			queue.setProperty("memoryStore.overflowToPersistent","false");
			PropertiesUtils.storeToFile( queue, new File(templatesDirectory, "queue-" + "DEFAULT_QUEUE_TEMPLATE" + ".properties") );
			
			
			Properties props = new Properties();
			props.load( FFMQServer.class.getResourceAsStream("/server.properties") );
			
			Properties usedProps = new Properties(props);
			usedProps.setProperty(FFMQCoreSettings.DESTINATION_DEFINITIONS_DIR, destinationDefinitionsDirectory.getAbsolutePath());
			usedProps.setProperty(FFMQCoreSettings.TEMPLATES_DIR, templatesDirectory.getAbsolutePath());
			usedProps.setProperty(FFMQCoreSettings.TEMPLATE_MAPPING_FILE, templateMapping.getAbsolutePath());
			usedProps.setProperty(FFMQCoreSettings.DEFAULT_DATA_DIR, defaultDataDirectory.getAbsolutePath());
			
			server = new FFMQServer("the-engine", new Settings(PropertiesUtils.flatten( usedProps )));
			
			started = server.start();
			
		}catch(Exception e){
			throw new ServerException(e);
		}
		
	}

	public void stop() throws ServerException {
		if(started) { 
			boolean success = server.shutdown();
			if(!success) throw new ServerException("An error occurred while stopping the server.");
		}
		
	}
	

	
}
