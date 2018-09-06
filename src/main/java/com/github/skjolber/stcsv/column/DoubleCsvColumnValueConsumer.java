package com.github.skjolber.stcsv.column;

import java.util.function.ObjDoubleConsumer;

public class DoubleCsvColumnValueConsumer<T> implements CsvColumnValueConsumer<T> {

	protected final ObjDoubleConsumer<T> setter;
		
	public DoubleCsvColumnValueConsumer(ObjDoubleConsumer<T> setter) {
		this.setter = setter;
	}

	@Override
	public void consume(T object, char[] array, int start, int end) {
		setter.accept(object, parseDouble(array, start, end));
	}

	public static double parseDouble(char[] array, int start, int end) {
		return Double.parseDouble(new String(array, start, end - start));
	}

}
