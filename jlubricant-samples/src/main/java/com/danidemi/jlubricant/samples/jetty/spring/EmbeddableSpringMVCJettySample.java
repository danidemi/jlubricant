package com.danidemi.jlubricant.samples.jetty.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.jetty.EmbeddableJetty;
import com.danidemi.jlubricant.embeddable.jetty.Feature;
import com.danidemi.jlubricant.embeddable.jetty.SpringDispatcherServletFeature;
import com.danidemi.jlubricant.embeddable.jetty.SpringFeature;
import com.danidemi.jlubricant.embeddable.jetty.WebAppFeature;
import com.danidemi.jlubricant.utils.wait.Wait;


@ComponentScan("com.danidemi.jlubricant.samples.jetty.spring")
@Configuration
public class EmbeddableSpringMVCJettySample  extends WebMvcConfigurerAdapter {

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
		WebAppFeature webAppFeature = new WebAppFeature(new String[]{"localhost"}, "/", true, "/jettySampleSpringMVC");
		webAppFeature.addFeature( springFeature() );
		jetty.addFeature( webAppFeature );
		//jetty.addFeature(  );
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
	
	@Bean
	RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
		return requestMappingHandlerMapping;
	}
	
	@Bean
	HandlerAdapter handler(){
		RequestMappingHandlerAdapter a = new RequestMappingHandlerAdapter();
		return a;
	}
	
	@Bean
	ViewResolver viewResolver() {
		InternalResourceViewResolver vr = new InternalResourceViewResolver();
		View v;
		vr.setPrefix("/WEB-INF/");
		vr.setSuffix(".jsp");
		vr.setViewClass(InternalResourceView.class);
		return vr;
	}
	
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
	
}
