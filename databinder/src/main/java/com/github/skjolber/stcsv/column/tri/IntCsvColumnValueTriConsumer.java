package com.github.skjolber.stcsv.column.tri;

import com.github.skjolber.stcsv.column.bi.IntCsvColumnValueConsumer;

public class IntCsvColumnValueTriConsumer<T, I> implements CsvColumnValueTriConsumer<T, I> {

	protected final ObjIntTriConsumer<T, I> setter;
		
	public IntCsvColumnValueTriConsumer(ObjIntTriConsumer<T, I> setter) {
		this.setter = setter;
	}

	public void consume(T object, I intermediate, char[] array, int start, int end) {
		setter.accept(object, intermediate, IntCsvColumnValueConsumer.parseInt(array, start, end));
	}
}
