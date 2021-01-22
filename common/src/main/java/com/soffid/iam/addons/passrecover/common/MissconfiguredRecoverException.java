package com.soffid.iam.addons.passrecover.common;


public class MissconfiguredRecoverException extends Exception {

	public MissconfiguredRecoverException() {
		super();
	}

	public MissconfiguredRecoverException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MissconfiguredRecoverException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissconfiguredRecoverException(String message) {
		super(message);
	}

	public MissconfiguredRecoverException(Throwable cause) {
		super(cause);
	}

}
