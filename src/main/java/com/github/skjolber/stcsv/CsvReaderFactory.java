package com.github.skjolber.stcsv;

import java.io.Reader;

/**
 * 
 * Dynamic CSV parser generator. Adapts the underlying implementation according 
 * to the first (header) line.
 */

public interface CsvReaderFactory<T> {

	CsvReader<T> create(Reader reader) throws Exception;
		
}
