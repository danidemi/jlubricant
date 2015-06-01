package com.danidemi.jlubricant.embeddable.jetty;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;


public class EmbeddableJettyTest {

	EmbeddableJetty jetty;
	static SSLSocketFactory defaultSSLSocketFactory;
	static HostnameVerifier defaultHostnameVerifier; 
	
	@BeforeClass public static void getDefaultHttps() {
		defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
		defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
	}
	
	@Before public void setUpJetty() throws ServerStartException {
		jetty = new EmbeddableJetty();	
	}
	
	@After public void tearDownJetty() throws ServerStopException {
		jetty.stop();		
	}
	
	@After public void resetHttps() {
		HttpsURLConnection.setDefaultSSLSocketFactory( defaultSSLSocketFactory );
		HttpsURLConnection.setDefaultHostnameVerifier( defaultHostnameVerifier );		
	}
	
	@Test public void shouldConnectThroughHttp() throws ServerStartException, IOException{
		
		jetty.addConnectivity(new HttpConnectivity("localhost", 8080, 30000));
		testConnection("http://localhost:8080/");
		
	}
	
	@Test public void shouldConnectThroughHttps() throws Exception{

		trustAllCerts();
		jetty.addConnectivity(new HttpsConnectivity(new File(getClass().getResource("/keystore").getFile()), "localhost", 8443, 30000, "pazzword"));
		testConnection("https://localhost:8443/");
		
	}	

	private void testConnection(String spec) throws ServerStartException, IOException, MalformedURLException {
		
		// given
		

		
		jetty.addFeature(new EasyFeature(){

			@Override
			protected void handle(HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				response.getWriter().write("Hello!");
				response.flushBuffer();
			}});
		jetty.start();
		
		// when
		String string = IOUtils.toString( new URL( spec ).openStream() );
		
		// then
		assertThat( string, equalTo("Hello!"));
		
	}
	
    static void trustAllCerts() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }

            }
        };
 
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
 
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
 
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
 
    }
		
}
