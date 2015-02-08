package com.danidemi.jlubricant.embeddable.jetty;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.filter.DelegatingFilterProxy;

/** Add the SpringSecurity filter to the web app. */
public class SpringSecurityFeature implements ServletContextListener, Feature {
	
	public SpringSecurityFeature() {
		super();
	}
	
	@Override
	public void install(EmbeddableJetty embeddableJetty) {
		embeddableJetty.getHandler().addEventListener(this);
	}

	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST);
		boolean isMatchAfter = true;
		String urlPatterns = "/*";
		servletContext
			.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class.getCanonicalName())
			.addMappingForUrlPatterns(dispatcherTypes, isMatchAfter, urlPatterns);
		;
	}
	
//	public void contextInitializedOld(ServletContextEvent sce) {
//		
//		WebSecurityConfigurerAdapter wsca = new WebSecurityConfigurerAdapter() {
//			
//		};
//		
//		AuthenticationManager authenticationManagerBean;
//		try {
//			authenticationManagerBean = wsca.authenticationManagerBean();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		FilterChain jeeFilterChain;
//		
//		DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
//		
//
//		//Filter theFilter = jetty.webApplicationContext.getBean("springSecurityFilterChain", Filter.class);
//		
//		ServletContext servletContext = sce.getServletContext();
//		
//		//Filter filter = theFilter;
//		
//		servletContext.addFilter("springSecurityFilterChain", delegatingFilterProxy);
//		
//	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}
	
}
