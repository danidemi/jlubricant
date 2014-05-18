package com.danidemi.jlubricant.embeddable;

/**
 * Base {@link Exception} for operations executed on a {@link EmbeddableServer}.
 * @author Daniele Demichelis
 */
public class ServerException extends Exception {

	private static final long serialVersionUID = 5791745114126072000L;

	public ServerException() {
		super();
	}

	public ServerException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerException(String message) {
		super(message);
	}

	public ServerException(Throwable cause) {
		super(cause);
	}

}
