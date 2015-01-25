package com.danidemi.jlubricant.samples;

import com.danidemi.jlubricant.embeddable.jetty.EmbeddableJetty;
import com.danidemi.jlubricant.embeddable.jetty.Feature;
import com.danidemi.jlubricant.embeddable.jetty.SpringFeature;
import com.danidemi.jlubricant.embeddable.jetty.WebAppFeature;
import com.danidemi.jlubricant.utils.wait.Wait;

public class JettySample {

	public static void main(String[] args) {
		
		try {
			EmbeddableJetty jetty = new EmbeddableJetty();
			String[] vh = null;
			String wcp = null;
			boolean dir = true;
			
			WebAppFeature feature = new WebAppFeature(vh, wcp, dir, "/jettySampleWebApp1");
			feature.setWebappContextPath("/");
			
			jetty.addFeature( feature );
			

			jetty.start();
			Wait.untilShutdown();			
			jetty.stop();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
