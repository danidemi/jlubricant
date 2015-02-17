package com.danidemi.jlubricant.example.embeddablejetty;

import org.junit.Test;

import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;
import com.danidemi.jlubricant.embeddable.jetty.EmbeddableJetty;
import com.danidemi.jlubricant.embeddable.jetty.WebAppFeature;
import com.danidemi.jlubricant.utils.wait.Wait;

public class Plain {
	
	@Test public void runASampleWebApp() throws ServerStartException, ServerStopException{
		
		EmbeddableJetty jetty = new EmbeddableJetty();
		jetty.addFeature( new WebAppFeature(null, "/", true, "/jettySampleWebApp1") );
		jetty.start();
		Wait.waitForMillis(10);			
		jetty.stop();
		
	}
	
}
