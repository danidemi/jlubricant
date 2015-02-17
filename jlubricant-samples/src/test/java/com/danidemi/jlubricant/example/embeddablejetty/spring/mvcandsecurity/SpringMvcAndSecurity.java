package com.danidemi.jlubricant.example.embeddablejetty.spring.mvcandsecurity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.authentication.AuthenticationManagerBeanDefinitionParser.NullAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.jetty.EmbeddableJetty;
import com.danidemi.jlubricant.embeddable.jetty.SpringDispatcherServletFeature;
import com.danidemi.jlubricant.embeddable.jetty.SpringFeature;
import com.danidemi.jlubricant.embeddable.jetty.SpringSecurityFeature;
import com.danidemi.jlubricant.embeddable.jetty.WebAppFeature;
import com.danidemi.jlubricant.utils.wait.Wait;

@EnableWebMvcSecurity
@ComponentScan({"com.danidemi.jlubricant.example.embeddablejetty.spring.mvcandsecurity", "com.danidemi.jlubricant.example.embeddablejetty.spring.webapp"})
@Configuration
public class SpringMvcAndSecurity extends WebMvcConfigurerAdapter {

	@Test public void runAnApp() throws ServerException {
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMvcAndSecurity.class);
		EmbeddableJetty jetty = ctx.getBean(EmbeddableJetty.class);
		try {
			jetty.start();
			Wait.waitForMillis(2);
			jetty.stop();
		}finally{
			ctx.close();
		}

		
	}
	
	@Bean
	EmbeddableJetty embeddableJetty(WebAppFeature webApp){
		EmbeddableJetty jetty = new EmbeddableJetty();
		jetty.addFeature( webApp );
		return jetty;
	}

	@Bean public WebAppFeature webApp(SpringFeature springFeature, SpringSecurityFeature security, SpringDispatcherServletFeature springDisptacher) {
		WebAppFeature webApp = new WebAppFeature(new String[]{"localhost"}, "/", true, "/jettySampleSpringMVC");
		webApp.addFeature( springFeature );
		webApp.addFeature( springDisptacher );
		webApp.addFeature( security );
		return webApp;
	}
	
	@Bean
	SpringFeature springFeature() {
		return new SpringFeature();
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
	SpringSecurityFeature f(){
		return new SpringSecurityFeature();
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
    
    @Bean
    WebSecurityConfigurer security() {
    	WebSecurityConfigurer webSecurityConfiguration = new WebSecurityConfigurerAdapter() {
    		@Override
    		protected AuthenticationManager authenticationManager()
    				throws Exception {
    			TestingAuthenticationProvider ap = new TestingAuthenticationProvider();
    			ArrayList<AuthenticationProvider> aps = new ArrayList<AuthenticationProvider>();
    			aps.add( ap );
    			aps.add( new AbstractUserDetailsAuthenticationProvider(){

					@Override
					protected void additionalAuthenticationChecks(
							UserDetails userDetails,
							UsernamePasswordAuthenticationToken authentication)
							throws AuthenticationException {
						// TODO Auto-generated method stub						
					}

					@Override
					protected UserDetails retrieveUser(String username,
							UsernamePasswordAuthenticationToken authentication)
							throws AuthenticationException {
						return new User(username, "password", true, true, true, true, null);
					}
    				
    			});
    			aps.add( new NullAuthenticationProvider() );
				return new ProviderManager( aps );
    		}
		};
		return webSecurityConfiguration;
    }
    	
}
