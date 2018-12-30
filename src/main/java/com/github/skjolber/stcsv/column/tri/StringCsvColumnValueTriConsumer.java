package com.github.skjolber.stcsv.column.tri;

public class StringCsvColumnValueTriConsumer<T, I> implements CsvColumnValueTriConsumer<T, I> {

	protected final TriConsumer<T, I, String> setter;

	public StringCsvColumnValueTriConsumer(TriConsumer<T, I, String> setter) {
		this.setter = setter;
	}

	@Override
	public void consume(T object, I intermediate, char[] array, int start, int end) {
		setter.accept(object, intermediate, new String(array, start, end - start));
	}
	
}
