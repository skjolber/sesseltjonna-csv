package com.github.skjolber.stcsv;

public class CsvMappingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CsvMappingException() {
		super();
	}

	public CsvMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CsvMappingException(String message, Throwable cause) {
		super(message, cause);
	}

	public CsvMappingException(String message) {
		super(message);
	}

	public CsvMappingException(Throwable cause) {
		super(cause);
	}

}
