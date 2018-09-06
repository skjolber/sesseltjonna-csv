package com.github.skjolber.stcsv.column;

import java.util.function.BiConsumer;

public class StringCsvColumnValueConsumer<T> implements CsvColumnValueConsumer<T> {

	protected final BiConsumer<T, String> setter;

	public StringCsvColumnValueConsumer(BiConsumer<T, String> setter) {
		this.setter = setter;
	}

	@Override
	public void consume(T object, char[] array, int start, int end) {
		setter.accept(object, new String(array, start, end - start));
	}
	
}
