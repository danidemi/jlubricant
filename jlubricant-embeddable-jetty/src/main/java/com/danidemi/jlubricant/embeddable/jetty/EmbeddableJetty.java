package com.danidemi.jlubricant.embeddable.jetty;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler.Context;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.danidemi.jlubricant.embeddable.EmbeddableServer;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;
import com.danidemi.jlubricant.utils.hoare.Preconditions;

/**
 * http://www.eclipse.org/jetty/documentation/current/embedding-jetty.html
 * 
 * @author danidemi
 */
public class EmbeddableJetty implements ApplicationContextAware, EmbeddableServer {
	
	private static final Logger log = LoggerFactory.getLogger(EmbeddableJetty.class);

	private Server server;
	private ApplicationContext mainSpringContext;
	GenericWebApplicationContext webApplicationContext;
	List<HttpConnectivity> connectivities = new ArrayList<>();
	private boolean dirAllowed = true;


	private Thread jettyThread;

	private String host;
	
	private List<Feature> features = new ArrayList<>();
		
	public void setHost(String host) {
		this.host = host;
	}
	
	public void addFeature(Feature f){
		this.features.add( f );
	}
	
	public void setFeatures(List<Feature> features){
		this.features.clear();
		this.features.addAll( features );
	}
		
	public boolean isDirAllowed() {
		return dirAllowed;
	}
	
	public void setDirAllowed(boolean dirAllowed) {
		this.dirAllowed = dirAllowed;
	}
	
	public void setConnectivities(List<HttpConnectivity> connectivities) {
		this.connectivities.addAll( connectivities );
	}
	
	public void addConnectivity( HttpConnectivity connectivity ){
		this.connectivities.add(connectivity);
	}
		
	@Override
	public void start() throws ServerStartException {
		
		if(features.isEmpty()) throw new IllegalArgumentException("Sorry, no features available.");

		
		server = new Server();
		
		// shared http config
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(8443);
		http_config.setOutputBufferSize(32768);
		
        
////		// === jetty-https.xml ===
//		http_config.addCustomizer(new SecureRequestCustomizer());        
//        SslContextFactory sslContextFactory = new SslContextFactory();
//        sslContextFactory.setKeyStorePath( "/tmp/keys/keystore" );
//
//        ServerConnector sslConnector = new ServerConnector(server,
//                new SslConnectionFactory(sslContextFactory, "http/1.1"),
//                new HttpConnectionFactory(http_config));
//        sslConnector.setPort(8443);
////        // === jetty-https.xml ===
        		
		for (Feature feature : features) {
			log.info("Installing feature " + feature);
			feature.install(this);
		}
			
		Connector[] connectors = new Connector[connectivities.size()];
		for(int i=0; i<connectivities.size(); i++){
			connectors[i] = connectivities.get(i).doInstall(server);
		}
		server.setConnectors(connectors);
		
		try {
			log.info("Starting Jetty...");
			
			final AtomicInteger k = new AtomicInteger(0);
			
			jettyThread = new Thread( new Runnable() {

				@Override
				public void run() {
					try {
											
				
						
						server.start();						
						
						StringBuffer stringBuffer = new StringBuffer();
						server.dump(stringBuffer);
						log.debug( stringBuffer.toString() );
						try{
							k.set(2);
							server.join();							
						}catch(InterruptedException ie){
							log.info("Stopping Jetty...");
						}
						
						log.info("Jetty stopped...");
						
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					
				}
				
			});
			jettyThread.setUncaughtExceptionHandler( new UncaughtExceptionHandler() {
				
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					EmbeddableJetty.this.uncaughtExceptionOnJettyThread(t, e);
				}
			} );
			jettyThread.start();
			
			while(k.get()==0){
				Thread.sleep(1000);
			}
			System.out.println("==========================" + k.get());

			log.info("Jetty Started.");
		} catch (Exception e) {
			throw new ServerStartException("An error occurred starting Jetty.", e);
		}
	}

	protected void uncaughtExceptionOnJettyThread(Thread t, Throwable e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() throws ServerStopException {
		try {
			log.info("Stopping Jetty...");
			server.stop();
			if(jettyThread!=null){
				jettyThread.interrupt();
			}
		} catch (Exception e) {
			throw new ServerStopException("An error occurred stopping Jetty.", e);
		}
	}


	protected void onContextInitialized(ServletContextEvent sce) {
		log.warn("jaxax.servlet context initialized");
	}
	
	protected void onContextDestroyed(ServletContextEvent sce) {
		log.warn("jaxax.servlet context destroyed");
	}


	@Override
	public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.mainSpringContext = applicationContext;
	}
	

	
	public void setHandler(Handler webapp) {
		server.setHandler(webapp);
	}
	
	public WebAppContext getHandler(){
		return (WebAppContext) server.getHandler();
	}
	
	public ServletContext createWebApp(
			String webappContextPath2, String webAppResourcePath2,
			String[] virtualHosts2, String[] welcomeFiles2) {
		Preconditions.condition("Please provide a context. A context specifies the path of the web app.", webappContextPath2 != null);
		
		
		// check whether the resource path to the web application really exists.
		URI uri;
		try {
			uri = new ClassPathResource(webAppResourcePath2).getURI();
			File webAppBaseDir = new File(uri);
			
			Preconditions.condition( format("Resource %s should be a directory, but it is not", uri) , webAppBaseDir.exists() && webAppBaseDir.isDirectory());
			
			log.info("Installing web app found at resource path '{}' resolved to URI '{}'.", webAppResourcePath2, uri);
		} catch (IOException e) {
			throw new RuntimeException("Unable to find web app files at resource path '" + webAppResourcePath2 + "'.", e);
		}
		
		WebAppContext webapp = new WebAppContext();
		
		if(virtualHosts2!=null){
			webapp.setVirtualHosts(virtualHosts2);		
		}
		
		webapp.setContextPath(webappContextPath2);
		
		webapp.setWar(uri.toString());
		
		// Disable directory listings if no index.html is found.
		webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed",
				String.valueOf(dirAllowed));
		if(welcomeFiles2!=null){
			webapp.setWelcomeFiles(welcomeFiles2);			
		}
		
		setHandler(webapp);
		
		Context servletContext = webapp.getServletContext();
		return servletContext;
		
	}

}
