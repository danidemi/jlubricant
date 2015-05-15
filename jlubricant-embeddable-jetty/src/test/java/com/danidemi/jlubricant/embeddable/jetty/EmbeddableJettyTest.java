package com.danidemi.jlubricant.embeddable.jetty;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;


public class EmbeddableJettyTest {

	EmbeddableJetty jetty; 
	
	@Before public void setUpJetty() throws ServerStartException {
		jetty = new EmbeddableJetty();	
	}
	
	@After public void tearDownJetty() throws ServerStopException {
		jetty.stop();		
	}	
	
	@Test public void shouldConnectThroughHttp() throws ServerStartException, IOException{
		
		jetty.addFeature(new EasyFeature(){

			@Override
			protected void handle(HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				response.getWriter().write("Hello!");
				response.flushBuffer();
			}});
		jetty.start();
		
		String string = IOUtils.toString( new URL( "http", "localhost", 8080, "/").openStream() );
		assertThat( string, equalTo("Hello!"));
		
	}
	
}
