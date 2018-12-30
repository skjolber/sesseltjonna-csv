package com.github.skjolber.stcsv;

import java.io.Reader;

/**
 * 
 * Dynamic CSV parser generator. Adapts the underlying implementation according 
 * to the first (header) line.
 */

public interface CsvReaderFactory2<T, I> {

	CsvReader<T> create(Reader reader, I intermediate) throws Exception;
	
}
