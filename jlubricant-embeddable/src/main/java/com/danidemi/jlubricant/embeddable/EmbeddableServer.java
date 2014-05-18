package com.danidemi.jlubricant.embeddable;

public interface EmbeddableServer {

	/**
	 * Blocks until the server started.
	 */
	public void start() throws ServerStartException;

	/**
	 * Blocks until the server stopped.
	 * After a server is stopped, it should not respond to any invocation.
	 */	
	public void stop() throws ServerStopException;
	
}
