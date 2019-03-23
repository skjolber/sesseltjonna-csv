package com.github.skjolber.stcsv.builder;

import com.github.skjolber.stcsv.CsvException;

public class CsvBuilderException extends CsvException {

	private static final long serialVersionUID = 1L;

	public CsvBuilderException() {
		super();
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
