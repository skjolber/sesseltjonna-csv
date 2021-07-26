package com.github.skjolber.stcsv;

public class CsvParseException extends CsvException {

	private static final long serialVersionUID = 1L;

	private final int index;
	
	public CsvParseException(int index) {
		super();
		this.index = index;
	}

	public CsvParseException(int index, String message, Throwable cause) {
		super(message, cause);
		this.index = index;
	}

	public CsvParseException(int index, String message) {
		super(message);
		this.index = index;
	}

	public CsvParseException(int index, Throwable cause) {
		super(cause);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
