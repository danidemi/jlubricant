package com.danidemi.jlubricant.embeddable.jetty;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

import com.asual.lesscss.LessServlet;

public class LessFeature implements Feature,  ServletContextListener {
	
	@Override
	public void install(EmbeddableJetty embeddableJetty) {
		embeddableJetty.getHandler().addEventListener( this );
		
	}	

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LessServlet lessServlet = new LessServlet();
		
		String servletName = "less";
		ServletContext servletContext = sce.getServletContext();
		
		
		
		ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, lessServlet);
		registration.setLoadOnStartup(1);
		registration.setInitParameter("compress", Boolean.FALSE.toString());
		registration.setInitParameter("lineNumbers", Boolean.FALSE.toString());
		registration.setInitParameter("cache", Boolean.FALSE.toString());
		
		registration.addMapping("*.css");
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// nothing to do
		
	}


	
}