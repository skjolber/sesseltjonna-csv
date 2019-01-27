package com.github.skjolber.stcsv;

import java.io.Reader;

public interface StaticCsvMapper<T> {

	CsvReader<T> newInstance(Reader reader);
	
	CsvReader<T> newInstance(Reader reader, char[] current, int offset, int length);

}
