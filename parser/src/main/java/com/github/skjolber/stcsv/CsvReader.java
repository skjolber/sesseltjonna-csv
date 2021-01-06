package com.github.skjolber.stcsv;

public interface CsvReader<T> extends AutoCloseable {

	T next() throws Exception;
	
}
