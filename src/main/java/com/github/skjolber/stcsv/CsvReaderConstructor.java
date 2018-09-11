package com.github.skjolber.stcsv;

import java.io.Reader;
import java.lang.reflect.Constructor;

/**
 * Wrapper for specific parser implementation
 *
 * @param <T> expected value object
 */

public class CsvReaderConstructor<T> {
	
	// https://stackoverflow.com/questions/28030465/performance-of-invoking-constructor-by-reflection
	
	private final Constructor<? extends AbstractCsvReader<T>> readerConstructor;
	private final Constructor<? extends AbstractCsvReader<T>> readerArrayConstructor;

	public CsvReaderConstructor(Class<? extends AbstractCsvReader<T>> cls) throws Exception {
		this.readerConstructor = cls.getConstructor(Reader.class);
		this.readerArrayConstructor  = cls.getConstructor(Reader.class, char[].class, int.class, int.class);
	}

	public AbstractCsvReader<T> newInstance(Reader reader) {
		try {
			return readerConstructor.newInstance(reader);
		} catch (Exception e) {
			throw new RuntimeException(); // should never happen
		}
	}
	
	public AbstractCsvReader<T> newInstance(Reader reader, char[] current, int offset, int length) {
		try {
			return readerArrayConstructor.newInstance(reader, current, offset, length);
		} catch (Exception e) {
			throw new RuntimeException(); // should never happen
		}
	}

	/*
	public AbstractCsvClassFactory<T> newInstance(Reader reader, boolean skipHeader) throws IOException {
		if(skipHeader) {
			do {
				int read = reader.read();
				if(read == -1) {
					return new NullCsvClassFactory<T>();
				} 
					
				if(read == (int)'\n') {
					break;
				}
			} while(true);
		}
		try {
			return constructor.newInstance(reader);
		} catch (Exception e) {
			throw new RuntimeException(); // should never happen
		}
	}
	*/
}