package com.danidemi.jlubricant.embeddable.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class HttpConnectivity {

	private int httpPort = 8080;
	private int idleTimeout = 30000;
	private String host;
	
	public HttpConnectivity() {
	}
	
	public HttpConnectivity(String host, int httpPort, int idleTimeout) {
		super();
		this.host = host;
		this.httpPort = httpPort;
		this.idleTimeout = idleTimeout;
	}

	public int getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}

	public long getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}



	public Connector doInstall(Server server){
		
		// shared http config
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(8443);
		http_config.setOutputBufferSize(32768);
		
		// HTTP connector #1
		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
		http.setPort(httpPort);
		http.setIdleTimeout(idleTimeout);
		if(host != null){
			http.setHost(host);			
		}
		
		return http;
		
	}
	
}
