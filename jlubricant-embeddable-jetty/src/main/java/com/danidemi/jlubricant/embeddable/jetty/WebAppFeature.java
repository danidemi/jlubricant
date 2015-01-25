package com.danidemi.jlubricant.embeddable.jetty;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.danidemi.jlubricant.utils.hoare.Preconditions;

/**
 * {@link Feature} that adds a web application to an {@link EmbeddableJetty}.
 * @author danidemi
 */
public class WebAppFeature implements Feature {
	
	private String[] virtualHosts = null;
	private String webappContextPath = "/";
	private boolean dirAllowed;
	private final String webAppResourcePath;
	private String[] welcomeFiles;
	
	private static final Logger log = LoggerFactory.getLogger(WebAppFeature.class);
	
	public WebAppFeature(String[] virtualHosts, String webappContextPath,
			boolean dirAllowed, String webAppResourcePath) {
		super();
		this.virtualHosts = virtualHosts;
		this.webappContextPath = webappContextPath;
		this.dirAllowed = dirAllowed;
		this.welcomeFiles = null;
		this.webAppResourcePath = webAppResourcePath;
	}
	
	public void setWelcomeFiles(String[] welcomeFiles) {
		this.welcomeFiles = welcomeFiles;
	}
	
	@Override
	public String toString() {
		return String.format("WebApp at path %1$s to context %2$s", webAppResourcePath,  webappContextPath);
	}

	/**
	 * The context path the default web app will be published under.
	 * Values as "/" or "/app" are accepted.
	 */
	public void setWebappContextPath(String webappContextPath) {
		this.webappContextPath = webappContextPath;
	}
	
	public void setVirtualHosts(List<String> virtualHosts) {
		this.virtualHosts = virtualHosts.toArray( new String[virtualHosts.size()] );
	}	

	@Override
	public void install(EmbeddableJetty embeddableJetty) {
		
		Preconditions.condition("Please provide a context. A context specifies the path of the web app.", webappContextPath != null);
		
		// check whether the resource path to the web application really exists.
		String resourceURI;
		try {
			resourceURI = new ClassPathResource(webAppResourcePath).getURI().toString();
			File webAppBaseDir = new File(new URI(resourceURI));
			
			Preconditions.condition( format("Resource %s should be a directory, but it is not", resourceURI) , webAppBaseDir.isDirectory());
			
			log.info("Installing web app found at resource path '{}' resolved to URI '{}'.", webAppResourcePath, resourceURI);
		} catch (IOException e) {
			throw new RuntimeException("Unable to find web app files at resource path '" + webAppResourcePath + "'.", e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		WebAppContext webapp = new WebAppContext();
		
		if(virtualHosts!=null){
			webapp.setVirtualHosts(virtualHosts);		
		}
		
		webapp.setContextPath(webappContextPath);
		
		webapp.setWar(resourceURI);
		
		// Disable directory listings if no index.html is found.
		webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed",
				String.valueOf(dirAllowed));
		if(welcomeFiles!=null){
			webapp.setWelcomeFiles(welcomeFiles);			
		}
		
		embeddableJetty.setHandler(webapp);		

	}

}
