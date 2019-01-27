package com.github.skjolber.stcsv;

import java.io.Reader;

public interface StaticCsvMapper2<T, D> {

	CsvReader<T> newInstance(Reader reader, D delegate);
	
	CsvReader<T> newInstance(Reader reader, char[] current, int offset, int length, D delegate);

}
