package com.danidemi.jlubricant.example.embeddablejetty.spring.mvc;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;
import com.danidemi.jlubricant.embeddable.jetty.EmbeddableJetty;
import com.danidemi.jlubricant.embeddable.jetty.Feature;
import com.danidemi.jlubricant.embeddable.jetty.SpringFeature;
import com.danidemi.jlubricant.embeddable.jetty.WebAppFeature;
import com.danidemi.jlubricant.utils.wait.Wait;

@Configuration
public class EmbeddableSpringJettySample {

	@Test
	public void runTheServer() throws ServerStartException, ServerStopException {

		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
				EmbeddableSpringJettySample.class);
		EmbeddableJetty jetty = ctx.getBean(EmbeddableJetty.class);
		try {
			jetty.start();
			Wait.waitForMillis(2);
			jetty.stop();
		} finally {
			ctx.close();
		}
	}

	@Bean
	EmbeddableJetty embeddableJetty() {
		EmbeddableJetty jetty = new EmbeddableJetty();
		jetty.addFeature(new WebAppFeature(null, "/", true,
				"/jettySampleWebApp1"));
		return jetty;
	}

}
