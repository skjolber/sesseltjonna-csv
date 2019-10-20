package com.github.skjolber.stcsv.databinder;

import java.io.Reader;

import com.github.skjolber.stcsv.CsvReader;

public interface StaticCsvMapper<T> {

	CsvReader<T> newInstance(Reader reader);
	
	CsvReader<T> newInstance(Reader reader, char[] current, int offset, int length);

}
