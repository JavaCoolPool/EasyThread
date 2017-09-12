package com.thread.asyc.exception;

import java.io.PrintStream;

public class AsynException extends RuntimeException {

	private static final long serialVersionUID = 383871777108677902L;
	
	public AsynException() {
		super();
	}

	public AsynException(String message) {
		super(message);
	}

	public AsynException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

	@Override
	public void printStackTrace() {
		super.printStackTrace();
	}

	@Override
	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);
	}

	
	
}
