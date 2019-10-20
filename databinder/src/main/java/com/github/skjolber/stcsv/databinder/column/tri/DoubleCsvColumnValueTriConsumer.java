package com.github.skjolber.stcsv.databinder.column.tri;

import com.github.skjolber.stcsv.databinder.column.bi.DoubleCsvColumnValueConsumer;

public class DoubleCsvColumnValueTriConsumer<T, I> implements CsvColumnValueTriConsumer<T, I> {

	protected final ObjDoubleTriConsumer<T, I> setter;
		
	public DoubleCsvColumnValueTriConsumer(ObjDoubleTriConsumer<T, I> setter) {
		this.setter = setter;
	}

	public void consume(T object, I intermediate, char[] array, int start, int end) {
		setter.accept(object, intermediate, DoubleCsvColumnValueConsumer.parseDouble(array, start, end));
	}
}
