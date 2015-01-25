package com.danidemi.jlubricant.samples;

import com.danidemi.jlubricant.embeddable.jetty.EmbeddableJetty;
import com.danidemi.jlubricant.embeddable.jetty.Feature;
import com.danidemi.jlubricant.embeddable.jetty.SpringFeature;
import com.danidemi.jlubricant.embeddable.jetty.WebAppFeature;
import com.danidemi.jlubricant.utils.wait.Wait;

public class EmbeddableJettySample {

	public static void main(String[] args) {
		
		try {
			
			EmbeddableJetty jetty = new EmbeddableJetty();
			jetty.addFeature( new WebAppFeature(null, "/", true, "/jettySampleWebApp1") );
			jetty.start();
			Wait.untilShutdown();			
			jetty.stop();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
