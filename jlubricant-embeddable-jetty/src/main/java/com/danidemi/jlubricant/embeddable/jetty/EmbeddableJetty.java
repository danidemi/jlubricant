package com.danidemi.jlubricant.embeddable.jetty;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler.Context;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

import com.asual.lesscss.LessOptions;
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
	private int httpPort = 8080;
	private int idleTimeout = 30000;
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
	
	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}
	
	@Override
	public void start() throws ServerStartException {

		
		server = new Server();
		
		// shared http config
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(8443);
		http_config.setOutputBufferSize(32768);
		
		// HTTP connector #1
		ServerConnector http = new ServerConnector(server,
				new HttpConnectionFactory(http_config));
		http.setPort(httpPort);
		http.setIdleTimeout(idleTimeout);
		if(host != null){
			http.setHost(host);			
		}
		
//		SslContextFactory sslContextFactory = new SslContextFactory();
//		sslContextFactory.setKeyStore(  );
		
		// HTTP connector #2
//		ServerConnector http2 = new ServerConnector(server,
//				new HttpConnectionFactory(http_config));
//		http2.setPort(9090);
//		http2.setIdleTimeout(30000);
		
//		WebAppContext webapp = new WebAppContext();
//		
//		if(virtualHosts!=null){
//			webapp.setVirtualHosts(virtualHosts);		
//		}
//		
//		webapp.setContextPath(webappContextPath);
//		
//		webapp.setWar(resourceURI);
//		
//		// Disable directory listings if no index.html is found.
//		webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed",
//				String.valueOf(dirAllowed));
		
		
		//webapp.addEventListener(new InitializerListener(this));
		//webapp.addEventListener(new RegisterLessServlet());
		//webapp.addEventListener(new SecurityEnable(this));
		
		for (Feature feature : features) {
			log.info("Installing feature " + feature);
			feature.install(this);
		}
		
//		// Create the root web application context and set it as a servlet
//		// attribute so the dispatcher servlet can find it.
//		webApplicationContext = new GenericWebApplicationContext();
//		webApplicationContext.setParent(mainSpringContext);
//		webApplicationContext.refresh();
//		webapp.setAttribute(
//				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
//				webApplicationContext);
		

		
		
		
		server.setConnectors(new Connector[] { http });
		
		try {
			log.info("Starting Jetty...");
			
			jettyThread = new Thread( new Runnable() {

				@Override
				public void run() {
					try {
						server.start();
						StringBuffer stringBuffer = new StringBuffer();
						server.dump(stringBuffer);
						log.debug( stringBuffer.toString() );
						try{
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
	

	
	public void setHandler(WebAppContext webapp) {
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
