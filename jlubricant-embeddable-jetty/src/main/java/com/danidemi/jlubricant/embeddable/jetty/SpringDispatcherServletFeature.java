package com.danidemi.jlubricant.embeddable.jetty;

import static java.lang.String.format;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.taglibs.standard.lang.jstl.ArraySuffix;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

import com.danidemi.jlubricant.utils.hoare.Preconditions;

public class SpringDispatcherServletFeature implements Feature, ApplicationContextAware {

	private String[] dispatcherServletSubPath = {"/"};
	private ConfigurableApplicationContext springCtxInWhichJettyRuns;
	
	/**
	 * The paths that will be taken in charge by Spring's DispatcherServlet.
	 * Values as {@code /appa/*} or {@code /} are ok. 
	 * @param dispatcherServletSubPath
	 */
	public SpringDispatcherServletFeature(String dispatcherServletSubPath) {
		super();
		this.dispatcherServletSubPath = new String[]{dispatcherServletSubPath};
	}
	
	/**
	 * The paths that will be taken in charge by Spring's DispatcherServlet.
	 * Values as {@code /appa/*} or {@code /} are ok. 
	 * @param dispatcherServletSubPath
	 */
	public SpringDispatcherServletFeature(String[] dispatcherServletSubPath) {
		super();
		this.dispatcherServletSubPath = dispatcherServletSubPath;
	}	

	@Override
	public String toString() {
		return String.format("SpringMVC Dispatcher Servlet at '%1$s'", dispatcherServletSubPath); 
	}
		
	public String[] getDispatcherServletSubPath() {
		return dispatcherServletSubPath;
	}
	
	@Override
	public void install(EmbeddableJetty embeddableJetty) {
		
//		webapp.setAttribute(
//		WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
//		webApplicationContext);
		
		if(springCtxInWhichJettyRuns == null){
			throw new RuntimeException(
					"Spring has not yet provided a reference to the context this feature is running in. "
					+ "Are you sure you gave Spring the chanche to inject a context into this feature ? "
					+ "If you are following a Java-based container configuration style, "
					+ "remember you have to create this feature in a @Bean annotated method, or something similar.");
		}
		
		GenericWebApplicationContext ctx = new GenericWebApplicationContext();
		ctx.setParent(springCtxInWhichJettyRuns);
		
		
		
//		embeddableJetty.getHandler().setAttribute(
//				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, 
//				ctx);
		
		Preconditions.condition("The Jetty instance where this feature is being installed does not have any handler.", embeddableJetty.getHandler()!=null);
		
		embeddableJetty.getHandler().addEventListener( 
				new InitializerListener(embeddableJetty) 
		);
		
	}	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.springCtxInWhichJettyRuns = (ConfigurableApplicationContext) applicationContext;
	}
	
	/**
	 * A {@link org.springframework.web.WebApplicationInitializer} suited to be used with Jetty.
	 *
	 * <p>{@link #createServletApplicationContext()} returns the one specified in the server.</p> 
	 * <p>{@link #getServletMappings()} return the ones specified in the server.</p>
	 * 
	 * @see AbstractDispatcherServletInitializer
	 */
	private class InitializerListener extends AbstractDispatcherServletInitializer implements ServletContextListener {

		private EmbeddableJetty jetty;
		private ServletContext servletContext;

		public InitializerListener(EmbeddableJetty jetty) {
			this.jetty = jetty;
		}

		/**
		 * Receives notification that the web application initialization process is starting.
		 */
		@Override
		public void contextInitialized(ServletContextEvent event) {
			try {
				servletContext = event.getServletContext();				
				onStartup(servletContext);
			} catch (Exception e) {
				logger.error("Failed to initialize web application", e);
				System.exit(0);
			}
		}
		
		
	    public void onStartup(ServletContext container) {
	        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
	        appContext.setBeanName("fakeEmptyContext");
	        //appContext.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");
	        String fakeEmptyContext = SpringUtils.fakeEmptyContext();
	        appContext.setParent(springCtxInWhichJettyRuns);
	        
//	        Object bean = springCtxInWhichJettyRuns.getBean("messageSource");
//	        if(bean == null){
//	        	springCtxInWhichJettyRuns.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
//					
//					@Override
//					public void postProcessBeanFactory(
//							ConfigurableListableBeanFactory beanFactory) throws BeansException {
//						ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
//						bean.setBasename("classpath:");
//						bean.setDefaultEncoding("UTF-8");
//						beanFactory.registerSingleton("messageSource", bean );
//						
//					}
//				});
//	        }
	        
	        appContext.setConfigLocation(fakeEmptyContext);
	        appContext.refresh();

	        ServletRegistration.Dynamic dispatcher =
	          container.addServlet("dispatcher", new DispatcherServlet(appContext));
	        dispatcher.setLoadOnStartup(1);
	        String[] dispatcherServletSubPath2 = SpringDispatcherServletFeature.this.dispatcherServletSubPath;
	        if(!ArrayUtils.isEmpty(dispatcherServletSubPath2)){
	        	for (String string : dispatcherServletSubPath2) {
	        		Set<String> alreadyExitingMappings = dispatcher.addMapping(string);
	        		if(CollectionUtils.isNotEmpty(alreadyExitingMappings)){
	        			throw new IllegalStateException(format("Mappings '%s' was already assigned to another servlet.", alreadyExitingMappings));
	        		}
				}
	        }
	        
	      }
	    

				
		@Override
		protected WebApplicationContext createServletApplicationContext() {
			XmlWebApplicationContext wac = new XmlWebApplicationContext();
			//wac.setServletConfig(null);
			//wac.setServletContext(servletContext);
			
			if(springCtxInWhichJettyRuns == null){
				throw new IllegalStateException("Still null");
			}
			wac.setParent(springCtxInWhichJettyRuns);
			
			return wac;
		}		
		
		@Override
		protected String[] getServletMappings() {
			return dispatcherServletSubPath;
		}
		

		@Override
		public void contextDestroyed(ServletContextEvent sce) {
			// nothing special
		}



		@Override
		protected WebApplicationContext createRootApplicationContext() {
//			if(springCtxInWhichJettyRuns == null){
//				throw new IllegalStateException("Still null");
//			}
//			XmlWebApplicationContext wac = new XmlWebApplicationContext();
//			wac.setParent(springCtxInWhichJettyRuns);
//			wac.setServletContext(this.servletContext);
//			return wac;

			return null;
			
			
			
		}
		
	}
	
}
