package com.danidemi.jlubricant.embeddable.jetty;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
	
	/**
	 * @param virtualHosts Arrays of virtual hosts to set. If null, no virtual hosts will be set.
	 * @param webappContextPath The context path of this web app.
	 * @param dirAllowed Whether directory listing is allowed.
	 * @param webAppResourcePath The resource path of the folder containing the web app.
	 */
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
		
		String webappContextPath2 = webappContextPath;
		String webAppResourcePath2 = webAppResourcePath;
		String[] virtualHosts2 = virtualHosts;
		String[] welcomeFiles2 = welcomeFiles;
		
		embeddableJetty.createWebApp(webappContextPath2, webAppResourcePath2,
				virtualHosts2, welcomeFiles2);	
		
		for (Feature feature : features) {
			feature.install(embeddableJetty);			
		}

	}



	
	private List<Feature> features = new ArrayList<Feature>();
	
	public void addFeature(Feature feat){
		features.add(feat);
	}
}
