package com.danidemi.jlubricant.embeddable.ffmq.core;

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
import com.danidemi.jlubricant.embeddable.ffmq.sample.MyProducer;
import com.danidemi.jlubricant.embeddable.ffmq.sample.MySyncReader;

/**
 * Embedded FFMQ sample. Highly inspired by the embeddable server 
 * <a href="http://timewalker74.github.io/ffmq/doc.html#III">provided in the FFMQ doc</a>.
 */
public class FFMQEmbeddableServer implements EmbeddableServer {
	
	private static final Logger log = LoggerFactory.getLogger(FFMQEmbeddableServer.class);
	
	private final String engineName;
	private final List<QueueDefinition> queueDefinitions;
	private final List<TopicDefinition> topicDefinitions;
	private final TcpListenerDescriptor tcpListener;
	private final Settings engineSettings;
	
	private FFMQEngine engine;
	private List<AbstractDestinationDefinition> destinationDefinitions;
	private boolean run;
	private Thread ffmqThread;

	public FFMQEmbeddableServer(String engineName,
			TcpListenerDescriptor tcpListener,
			List<QueueDefinition> queueDefinitions,
			List<TopicDefinition> topicDefinitions, Settings engineSettings) {
		super();

		checkNotBlank(engineName, "Engine name cannot be blank.");
		checkNotNull(tcpListener, "Please provide a listener.");
		checkNotNull(queueDefinitions, "Please provide some queues.");
		checkNotNull(engineSettings, "Please provide engine settings");

		this.engineName = engineName;
		this.tcpListener = tcpListener;

		// this.destinationDefinitions = new
		// ArrayList<AbstractDestinationDefinition>();
		// destinationDefinitions.addAll( queueDefinitions );
		// destinationDefinitions.addAll( topicDefinitions );

		this.queueDefinitions = queueDefinitions;
		this.topicDefinitions = topicDefinitions;
		this.engineSettings = engineSettings;
	}

	public void start() throws ServerException {

		if (ffmqThread != null)
			throw new ServerException("Already running.");

		final Object lock = new Object();

		ffmqThread = new Thread(new Runnable() {

			public void run() {
				try {
					// Create engine settings
					// Create the engine itself
					engine = new FFMQEngine(engineName, engineSettings);
					// -> myLocalEngineName will be the engine name.
					// - It should be unique in a given JVM
					// - This is the name to be used by local clients to
					// establish
					// an internal JVM connection (high performance)
					// Use the following URL for clients :
					// vm://myLocalEngineName
					//

					// Deploy the engine
					log.info("Deploying engine : {}", engine.getName());
					engine.deploy();
					// - The FFMQ engine is not functional until deployed.
					// - The deploy operation re-activates all persistent queues
					// and recovers them if the engine was not properly closed.
					// (May take some time for large queues)

					// Adding a TCP based client listener
					log.info("Starting listener ...");
					TcpListener listener = tcpListener.build(engine);
					listener.start();
					// tcpListener.start();

					// This is how you can programmatically define a new queue
					for (QueueDefinition queueDefinition : queueDefinitions) {
						if (!engine.getDestinationDefinitionProvider().hasQueueDefinition(queueDefinition.getName())) {

							if (queueDefinition.getDataFolder() == null) {
								queueDefinition.setDataFolder(engine.getSetup()
										.getDefaultDataDir());
							}

							queueDefinition.check();
							engine.createQueue(queueDefinition);
						}
					}

					for (TopicDefinition topicDefinition : topicDefinitions) {
						if (!engine.getDestinationDefinitionProvider()
								.hasTopicDefinition(topicDefinition.getName())) {

							if (topicDefinition.getDataFolder() == null) {
								topicDefinition.setDataFolder(engine.getSetup()
										.getDefaultDataDir());
							}

							topicDefinition.check();
							engine.createTopic(topicDefinition);
						}
					}

					// Run until interrupted
					log.info("Running ...");
					run = true;

					// Notify external that it started.
					synchronized (lock) {
						lock.notify();
					}

					while (run) {
						try {
							Thread.sleep(Long.MAX_VALUE);
						} catch (InterruptedException ie) {
							// nothing special
						}
					}

					// Stopping the listener
					log.info("Stopping listener ...");
					listener.stop();

					// Undeploy the engine
					log.info("Undeploying engine ...");
					engine.undeploy();
					// - It is important to properly shutdown the engine
					// before stopping the JVM to make sure current transactions
					// are nicely completed and storages properly closed.

					log.info("Stopped.");
				} catch (Exception e) {
					// Oops
					e.printStackTrace();
				}

			}

		});

		synchronized (lock) {
			ffmqThread.start();
			try {
				lock.wait();
			} catch (InterruptedException e) {
				throw new ServerException(e);
			}
		}

	}

	public void stop() throws ServerException {
		run = false;
		ffmqThread.interrupt();
		try {
			ffmqThread.join(5000);
		} catch (InterruptedException e) {
			new ServerException(
					"An exception occurred while waiting the FFMQ thread to stop.",
					e);
		}
		if (run == true) {
			new ServerException(
					"Failed to stop in a reasonable amount of time.");
		}
	}
	
	public Context jndiCtx() throws NamingException {
		
		// Create and initialize a JNDI context
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,FFMQConstants.JNDI_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, "tcp://localhost:10002");
		Context context = new InitialContext(env);
		return context;
		
	}

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