package com.github.skjolber.stcsv.column.tri;

@FunctionalInterface
public interface CsvColumnValueTriConsumer<T, I> {

	/**
	 * Generic interface for parsing values from a character array.
	 * 
	 * @param object target
	 * @param intermediate intermediate helper / processor
	 * @param array character array
	 * @param start start index, inclusive
	 * @param end end index, exclusive
	 */
	
	void consume(T object, I intermediate, char[] array, int start, int end);

}
