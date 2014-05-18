package com.danidemi.jlubricant.embeddable;

public class ServerStartException extends ServerException {

	private static final long serialVersionUID = -9071755614370578557L;

	public ServerStartException() {
		super();
	}

	public ServerStartException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServerStartException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerStartException(String message) {
		super(message);
	}

	public ServerStartException(Throwable cause) {
		super(cause);
	}

}
