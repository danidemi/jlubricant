package com.danidemi.jlubricant.embeddable.jetty;

import javax.servlet.ServletContext;

public interface ContextAvailableListener {

	void onContextAvailable(ServletContext servletContext);

}
