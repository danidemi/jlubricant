package com.danidemi.jlubricant.embeddable.ffmq.sample;

import static com.danidemi.jlubricant.utils.hoare.Arguments.checkNotBlank;
import static com.danidemi.jlubricant.utils.hoare.Arguments.checkNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.timewalker.ffmq3.FFMQConstants;
import net.timewalker.ffmq3.FFMQCoreSettings;
import net.timewalker.ffmq3.listeners.tcp.io.TcpListener;
import net.timewalker.ffmq3.local.FFMQEngine;
import net.timewalker.ffmq3.management.destination.definition.AbstractDestinationDefinition;
import net.timewalker.ffmq3.management.destination.definition.QueueDefinition;
import net.timewalker.ffmq3.management.destination.definition.TopicDefinition;
import net.timewalker.ffmq3.utils.Settings;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danidemi.jlubricant.embeddable.EmbeddableServer;
import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ffmq.core.FFMQEmbeddableServer;
import com.danidemi.jlubricant.embeddable.ffmq.core.SingleFolderConfiguration;
import com.danidemi.jlubricant.embeddable.ffmq.core.TcpListenerDescriptor;

public class FFMQEmbeddableServerSample {
	
	public static void main(String[] args) {
		String mengineName = "engine";

		QueueDefinition queue1 = new QueueDefinition();
		queue1.setMaxBlockCount(1024);
		queue1.setName("queue1");
		queue1.setAutoExtendAmount(1024);
		queue1.setInitialBlockCount(1024);
		List<QueueDefinition> mqueueDefinitions = Arrays.asList(queue1);

		TopicDefinition topic1 = new TopicDefinition();
		topic1.setName("topic1");
		topic1.setMaxBlockCount(1024);
		topic1.setAutoExtendAmount(1024);
		topic1.setInitialBlockCount(1024);
		List<TopicDefinition> mtopicDefinitions = Arrays.asList(topic1);

		SingleFolderConfiguration singleFolderConfiguration = new SingleFolderConfiguration(new File(
				FileUtils.getTempDirectory(), "ffmq"));
		
		Properties properties = singleFolderConfiguration.getProperties();
		
		
		Settings mengineSettings = new Settings(properties);

		TcpListenerDescriptor mtcpListener = new TcpListenerDescriptor(
				"0.0.0.0", 10002, new Settings());
		FFMQEmbeddableServer emb = new FFMQEmbeddableServer(mengineName,
				mtcpListener, mqueueDefinitions, mtopicDefinitions,
				mengineSettings);

		try {

			Context context = emb.jndiCtx();

			emb.start();

//			MyProducer myProducer = new MyProducer("pr1", context, 5, "queue1");
//			myProducer.start();
//
//			MySyncReader myReader = new MySyncReader("rx1", context, "queue1");
//			myReader.start();
			
			MySyncReader myReader = new MySyncReader("rx1", context, "queueX");
			myReader.start();			

			Thread.sleep(10 * 1000);

			emb.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// private Settings createEngineSettings()
	// {
	// // Various ways of creating engine settings
	//
	// // 1 - From a properties file
	// Properties externalProperties = new Properties();
	// try
	// {
	// FileInputStream in = new
	// FileInputStream("../conf/ffmq-server.properties");
	// externalProperties.load(in);
	// in.close();
	// }
	// catch (Exception e)
	// {
	// throw new RuntimeException("Cannot load external properties",e);
	// }
	// Settings settings = new Settings(externalProperties);
	//
	// // 2 - Explicit Java code
	// // Settings settings = new Settings();
	// //
	// //
	// settings.setStringProperty(FFMQCoreSettings.DESTINATION_DEFINITIONS_DIR,
	// ".");
	// // settings.setStringProperty(FFMQCoreSettings.BRIDGE_DEFINITIONS_DIR,
	// ".");
	// // settings.setStringProperty(FFMQCoreSettings.TEMPLATES_DIR, ".");
	// // settings.setStringProperty(FFMQCoreSettings.DEFAULT_DATA_DIR, ".");
	// // ...
	//
	// return settings;
	// }

}