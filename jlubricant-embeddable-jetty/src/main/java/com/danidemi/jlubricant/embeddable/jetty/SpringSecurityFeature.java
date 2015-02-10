package com.danidemi.jlubricant.embeddable.jetty;

import java.util.EnumSet;
import java.util.Enumeration;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.collections4.EnumerationUtils;
import org.springframework.web.context.WebApplicationContext;
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
		
		Object attribute = servletContext.getAttribute( WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE );
		if(attribute == null){
			Enumeration<String> attributeNames = servletContext.getAttributeNames();
			throw new IllegalStateException( WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + " not ready. Available attributes: " + EnumerationUtils.toList(attributeNames) );
		}
		
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
