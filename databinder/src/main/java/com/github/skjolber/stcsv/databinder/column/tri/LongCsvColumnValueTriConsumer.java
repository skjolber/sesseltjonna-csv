package com.github.skjolber.stcsv.databinder.column.tri;

import com.github.skjolber.stcsv.databinder.column.bi.LongCsvColumnValueConsumer;

public class LongCsvColumnValueTriConsumer<T, I> implements CsvColumnValueTriConsumer<T, I> {

	protected final ObjLongTriConsumer<T, I> setter;
		
	public LongCsvColumnValueTriConsumer(ObjLongTriConsumer<T, I> setter) {
		this.setter = setter;
	}

	public void consume(T object, I intermediate, char[] array, int start, int end) {
		setter.accept(object, intermediate, LongCsvColumnValueConsumer.parseLong(array, start, end));
	}
}
