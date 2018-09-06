package com.github.skjolber.stcsv;

public class NullCsvClassFactory<T> extends AbstractCsvClassFactory<T> {

	public NullCsvClassFactory() {
		super(null, 0);
	}

	@Override
	public T next() throws Exception {
		return null;
	}
}
