package com.danidemi.jlubricant.embeddable.jetty;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;

public class WebAppFeature implements Feature {
	
	private String[] virtualHosts = null;
	private String webappContextPath = "/";
	private char[] dirAllowed;
	private String springResourcePathForWebApp = "webapp";
	private String[] welcomeFiles;
	
	public WebAppFeature(String[] virtualHosts, String webappContextPath,
			char[] dirAllowed) {
		super();
		this.virtualHosts = virtualHosts;
		this.webappContextPath = webappContextPath;
		this.dirAllowed = dirAllowed;
		this.welcomeFiles = null;
	}
	
	public void setWelcomeFiles(String[] welcomeFiles) {
		this.welcomeFiles = welcomeFiles;
	}
	
	@Override
	public String toString() {
		return String.format("WebApp at path %1$s to context %2$s", springResourcePathForWebApp,  webappContextPath);
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
		
		
		// where in the classpath can be found a web app structure
		
		String resourceURI;
		try {
			resourceURI = new ClassPathResource(springResourcePathForWebApp).getURI().toString();
		} catch (IOException e) {
			throw new RuntimeException("Unable to find web app files at resource '" + springResourcePathForWebApp + "'", e);
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
