package com.danidemi.jlubricant.embeddable;


public interface EmbeddableServer {

	/**
	 * Blocks until the server started.
	 */
	public void start() throws ServerException;

	/**
	 * Blocks until the server stopped.
	 * After a server is stopped, it should not respond to any invocation.
	 * @throws ServerException 
	 */	
	public void stop() throws ServerException;
	
}
