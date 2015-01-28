package com.danidemi.jlubricant.samples;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.jetty.EmbeddableJetty;
import com.danidemi.jlubricant.embeddable.jetty.Feature;
import com.danidemi.jlubricant.embeddable.jetty.SpringFeature;
import com.danidemi.jlubricant.embeddable.jetty.WebAppFeature;
import com.danidemi.jlubricant.utils.wait.Wait;

@Configuration
public class EmbeddableSpringJettySample {

	public static void main(String[] args) {
		
		ApplicationContext ctx = new AnnotationConfigApplicationContext(EmbeddableSpringJettySample.class);
		    EmbeddableJetty jetty = ctx.getBean(EmbeddableJetty.class);
		    try {
				jetty.start();
				Wait.forever();
				jetty.stop();
			} catch (ServerException e) {
				e.printStackTrace();
			}
	}
	
	@Bean
	EmbeddableJetty embeddableJetty(){
		EmbeddableJetty jetty = new EmbeddableJetty();
		jetty.addFeature( new WebAppFeature(null, "/", true, "/jettySampleWebApp1") );
		return jetty;
	}
	
}
