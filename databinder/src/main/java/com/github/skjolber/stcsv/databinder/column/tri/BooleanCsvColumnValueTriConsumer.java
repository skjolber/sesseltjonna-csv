package com.github.skjolber.stcsv.databinder.column.tri;

import com.github.skjolber.stcsv.databinder.column.bi.BooleanCsvColumnValueConsumer;

public class BooleanCsvColumnValueTriConsumer<T, I> implements CsvColumnValueTriConsumer<T, I> {

	protected final ObjBooleanTriConsumer<T, I> setter;
		
	public BooleanCsvColumnValueTriConsumer(ObjBooleanTriConsumer<T, I> setter) {
		this.setter = setter;
	}

	@Override
	public void consume(T object, I intermediate, char[] array, int start, int end) {
		setter.accept(object, intermediate,
				BooleanCsvColumnValueConsumer.parseBoolean(array, start, end)
				);
		
	}

}
