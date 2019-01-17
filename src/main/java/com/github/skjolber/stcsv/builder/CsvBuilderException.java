package com.github.skjolber.stcsv.builder;

public class CsvBuilderException extends Exception {

	public CsvBuilderException() {
		super();
	}

	public CsvBuilderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CsvBuilderException(String message, Throwable cause) {
		super(message, cause);
	}

	public CsvBuilderException(String message) {
		super(message);
	}

	public CsvBuilderException(Throwable cause) {
		super(cause);
	}

}
