package com.github.skjolber.stcsv.column;

public class BooleanCsvColumnValueConsumer<T> implements CsvColumnValueConsumer<T> {

	protected final ObjBooleanConsumer<T> setter;
		
	public BooleanCsvColumnValueConsumer(ObjBooleanConsumer<T> setter) {
		this.setter = setter;
	}

	@Override
	public void consume(T object, char[] array, int start, int end) {
		
		setter.accept(object, 
				parseBoolean(array, start, end)
				);
	}

	public static boolean parseBoolean(char[] array, int start, int end) {
		return end - start == 4 && 
		(array[start] == 't' || array[start] == 'T') &&
		(array[++start] == 'r' || array[start] == 'R') &&
		(array[++start] == 'u' || array[start] == 'U') &&
		(array[++start] == 'e' || array[start] == 'E');
	}

}
