package com.github.skjolber.stcsv.sa;

import java.io.Reader;

import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.builder.StringArrayCsvReaderBuilder;

public abstract class StringArrayCsvReader extends AbstractCsvReader<String[]> {

	public static StringArrayCsvReaderBuilder builder() {
		return new StringArrayCsvReaderBuilder();
	}
	
	public StringArrayCsvReader(Reader reader, int length) {
		super(reader, length);
	}

	public StringArrayCsvReader(Reader reader, char[] current, int offset, int length) {
		super(reader, current, offset, length);
	}

}
