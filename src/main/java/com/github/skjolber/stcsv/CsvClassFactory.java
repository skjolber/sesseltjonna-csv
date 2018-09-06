package com.github.skjolber.stcsv;

public interface CsvClassFactory<T> {

	T next() throws Exception;
	
}
