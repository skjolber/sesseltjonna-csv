package com.github.skjolber.stcsv;

public class CsvException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CsvException() {
		super();
	}

	public CsvException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CsvException(String message, Throwable cause) {
		super(message, cause);
	}

	public CsvException(String message) {
		super(message);
	}

	public CsvException(Throwable cause) {
		super(cause);
	}

}
