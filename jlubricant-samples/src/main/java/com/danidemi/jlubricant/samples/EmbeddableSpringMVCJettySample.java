package com.danidemi.jlubricant.samples;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.jetty.EmbeddableJetty;
import com.danidemi.jlubricant.embeddable.jetty.Feature;
import com.danidemi.jlubricant.embeddable.jetty.SpringDispatcherServletFeature;
import com.danidemi.jlubricant.embeddable.jetty.SpringFeature;
import com.danidemi.jlubricant.embeddable.jetty.WebAppFeature;
import com.danidemi.jlubricant.utils.wait.Wait;

@Configuration
public class EmbeddableSpringMVCJettySample {

	public static void main(String[] args) {
		
		ApplicationContext ctx = new AnnotationConfigApplicationContext(EmbeddableSpringMVCJettySample.class);
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
		jetty.addFeature( springFeature() );
		return jetty;
	}

	/* 
	 * Please note that the SpringDispatcherServletFeature should be
	 * returned through a @Bean annotated method in order to let Spring inject
	 * the context in it.
	 */
	@Bean
	SpringDispatcherServletFeature springFeature() {
		return new SpringDispatcherServletFeature("/");
	}
	
}
