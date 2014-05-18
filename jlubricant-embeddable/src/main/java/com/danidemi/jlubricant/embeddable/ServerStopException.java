package com.danidemi.jlubricant.embeddable;

public class ServerStopException extends ServerException {

	private static final long serialVersionUID = 633200033705652785L;

	public ServerStopException() {
		super();
	}

	public ServerStopException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServerStopException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerStopException(String message) {
		super(message);
	}

	public ServerStopException(Throwable cause) {
		super(cause);
	}

}
