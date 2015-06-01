package com.danidemi.jlubricant.embeddable.jetty;

import java.io.File;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpsConnectivity implements Connectivity {

	private int httpPort = 8443;
	private int idleTimeout = 30000;
	private String host;
	private File keystoreFile;
	private String trustStoreAndKeyStorePassword;
	
	public HttpsConnectivity() {
	}
	
	public HttpsConnectivity(File keystoreFile, String host, int httpPort, int idleTimeout, String trustStoreAndKeyStorePassword) {
		super();
		
		if(!keystoreFile.exists() || !keystoreFile.isFile()) throw new IllegalArgumentException( keystoreFile.getAbsolutePath() );
		
		this.host = host;
		this.httpPort = httpPort;
		this.idleTimeout = idleTimeout;
		this.keystoreFile = keystoreFile;
		this.trustStoreAndKeyStorePassword = trustStoreAndKeyStorePassword;
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


	@Override
	public Connector doInstall(Server server){
		
        // HTTP Configuration
        // HttpConfiguration is a collection of configuration information
        // appropriate for http and https. The default scheme for http is
        // <code>http</code> of course, as the default for secured http is
        // <code>https</code> but we show setting the scheme to show it can be
        // done. The port for secured communication is also set here.
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(this.httpPort);
        http_config.setOutputBufferSize(32768);
  
        // SSL Context Factory for HTTPS
        // SSL requires a certificate so we configure a factory for ssl contents
        // with information pointing to what keystore the ssl connection needs
        // to know about. Much more configuration is available the ssl context,
        // including things like choosing the particular certificate out of a
        // keystore to be used.
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
		sslContextFactory.setKeyStorePassword(trustStoreAndKeyStorePassword);
        sslContextFactory.setTrustStorePath(keystoreFile.getAbsolutePath());
        sslContextFactory.setTrustStorePassword(trustStoreAndKeyStorePassword);
 
        // HTTPS Configuration
        // A new HttpConfiguration object is needed for the next connector and
        // you can pass the old one as an argument to effectively clone the
        // contents. On this HttpConfiguration object we add a
        // SecureRequestCustomizer which is how a new connector is able to
        // resolve the https connection before handing control over to the Jetty
        // Server.
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());
 
        // HTTPS connector
        // We create a second ServerConnector, passing in the http configuration
        // we just made along with the previously created ssl context factory.
        // Next we set the port and a longer idle timeout.
        ServerConnector https = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory,HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        https.setPort(this.httpPort);
        https.setIdleTimeout(idleTimeout);
        
        return https;
		
	}
	
}
