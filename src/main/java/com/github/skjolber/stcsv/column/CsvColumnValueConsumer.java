package com.github.skjolber.stcsv.column;

@FunctionalInterface
public interface CsvColumnValueConsumer<T> {

	/**
	 * Generic interface for parsing values from a character array.
	 * 
	 * @param object target
	 * @param array character array
	 * @param start start index, inclusive
	 * @param end end index, exclusive
	 */
	
	void consume(T object, char[] array, int start, int end);

}
