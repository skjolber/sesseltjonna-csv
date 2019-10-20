package com.github.skjolber.stcsv.databinder;

import java.io.Reader;
import java.lang.reflect.Constructor;

import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.EmptyCsvReader;

/**
 * 
 * Static CSV parser generator - for specific parser implementation
 * <br><br>
 * Thread-safe.
 * @param <T> csv line output value 
 * @param <T> intermediate processor
 */

public class DefaultStaticCsvMapper2<T, D> implements StaticCsvMapper2<T, D>{
	
	// https://stackoverflow.com/questions/28030465/performance-of-invoking-constructor-by-reflection
	private final Constructor<? extends AbstractCsvReader<T>> readerConstructor;
	private final Constructor<? extends AbstractCsvReader<T>> readerArrayConstructor;

	public DefaultStaticCsvMapper2(Class<? extends AbstractCsvReader<T>> cls, Class<D> delegate) throws Exception {
		if(cls != null) {
			this.readerConstructor = cls.getConstructor(Reader.class, delegate);
			this.readerArrayConstructor  = cls.getConstructor(Reader.class, char[].class, int.class, int.class, delegate);
		} else {
			this.readerConstructor = null;
			this.readerArrayConstructor = null;
		}	}

	public AbstractCsvReader<T> newInstance(Reader reader, D delegate) {
		try {
			if(readerConstructor != null) {
				return readerConstructor.newInstance(reader, delegate);
			}
			return new EmptyCsvReader<>();
		} catch (Exception e) {
			throw new RuntimeException(e); // should never happen
		}
	}
	
	public AbstractCsvReader<T> newInstance(Reader reader, char[] current, int offset, int length, D delegate) {
		try {
			if(readerArrayConstructor != null) {
				return readerArrayConstructor.newInstance(reader, current, offset, length, delegate);
			} 
			return new EmptyCsvReader<>();
		} catch (Exception e) {
			throw new RuntimeException(e); // should never happen
		}
	}
}