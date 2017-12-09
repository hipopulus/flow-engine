package me.hipoplar.flow;

public class FlowException extends RuntimeException {
	private static final long serialVersionUID = -7571719935661827670L;

	public FlowException() {
		super();
	}

	public FlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FlowException(String message, Throwable cause) {
		super(message, cause);
	}

	public FlowException(String message) {
		super(message);
	}

	public FlowException(Throwable cause) {
		super(cause);
	}
}
