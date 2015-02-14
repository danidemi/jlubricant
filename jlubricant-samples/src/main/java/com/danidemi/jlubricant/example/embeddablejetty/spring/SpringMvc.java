package com.danidemi.jlubricant.example.embeddablejetty.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.jetty.EmbeddableJetty;
import com.danidemi.jlubricant.embeddable.jetty.Feature;
import com.danidemi.jlubricant.embeddable.jetty.SpringDispatcherServletFeature;
import com.danidemi.jlubricant.embeddable.jetty.SpringFeature;
import com.danidemi.jlubricant.embeddable.jetty.WebAppFeature;
import com.danidemi.jlubricant.utils.wait.Wait;


@ComponentScan("com.danidemi.jlubricant.example.embeddablejetty.spring")
@Configuration
public class SpringMvc  extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMvc.class);
		EmbeddableJetty jetty = ctx.getBean(EmbeddableJetty.class);
		try {
			jetty.start();
			Wait.forever();
			jetty.stop();
		} catch (ServerException e) {
			e.printStackTrace();
		}finally{
			ctx.close();			
		}
		
	}
	
	@Bean SpringFeature springFeature() {
		return new SpringFeature();
	}
	
	@Bean
	EmbeddableJetty embeddableJetty(SpringDispatcherServletFeature springDispatcherFeature, Feature springFeature, Feature webAppFeature){
		EmbeddableJetty jetty = new EmbeddableJetty();
		jetty.addFeature( webAppFeature );
		jetty.addFeature( springFeature );
		jetty.addFeature( springDispatcherFeature );
		//jetty.addFeature(  );
		return jetty;
	}

	@Bean
	WebAppFeature webAppFeature() {
		return new WebAppFeature(new String[]{"localhost"}, "/", true, "/jettySampleSpringMVC");
	}

	/* 
	 * Please note that the SpringDispatcherServletFeature should be
	 * returned through a @Bean annotated method in order to let Spring inject
	 * the context in it.
	 */
	@Bean
	SpringDispatcherServletFeature springDispatcherFeature() {
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
