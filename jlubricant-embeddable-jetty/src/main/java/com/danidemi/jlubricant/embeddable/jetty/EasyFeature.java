package com.danidemi.jlubricant.embeddable.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public abstract class EasyFeature implements Feature {

	@Override
	public void install(EmbeddableJetty jetty) {
		Handler h = new AbstractHandler() {
			
			@Override
			public void handle(String target, Request baseRequest,
					HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
				EasyFeature.this.handle(request, response);
				
			}
		};
		jetty.setHandler(h);
		
	}

	protected abstract void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

}
