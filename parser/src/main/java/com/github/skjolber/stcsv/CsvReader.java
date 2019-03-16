package com.github.skjolber.stcsv;

public interface CsvReader<T> {

	T next() throws Exception;
	
}
