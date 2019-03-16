package com.github.skjolber.stcsv;

public class EmptyCsvReader<T> extends AbstractCsvReader<T> {

	public EmptyCsvReader() {
		super(null, 0);
	}

	@Override
	public T next() throws Exception {
		return null;
	}
}
