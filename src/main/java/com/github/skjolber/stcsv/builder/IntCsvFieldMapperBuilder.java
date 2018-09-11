package com.github.skjolber.stcsv.builder;

import java.util.function.ObjIntConsumer;

import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.IntCsvColumnValueConsumer;

public class IntCsvFieldMapperBuilder<T> extends AbstractCsvFieldMapperBuilder<T> {

	protected ObjIntConsumer<T> consumer;
	protected ObjIntConsumer<T> setter;

	public IntCsvFieldMapperBuilder(CsvMappingBuilder<T> parent, String name) {
		super(parent, name);
	}

	public IntCsvFieldMapperBuilder<T> consumer(ObjIntConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}
	
	public IntCsvFieldMapperBuilder<T> setter(ObjIntConsumer<T> setter) {
		this.setter = setter;
		
		return this;
	}	
	
	public IntCsvFieldMapperBuilder<T> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public IntCsvFieldMapperBuilder<T> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public IntCsvFieldMapperBuilder<T> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public IntCsvFieldMapperBuilder<T> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public IntCsvFieldMapperBuilder<T> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
	}

	@Override
	public CsvColumnValueConsumer<T> getValueConsumer() {
		if(consumer != null) {
			return new IntCsvColumnValueConsumer<>(consumer);
		}
		return null;
	}

	@Override
	protected Class<?> getColumnClass() {
		return int.class;
	}

	@Override
	protected void invokeSetter(T value) {
		setter.accept(value, 0);
	}

	@Override
	protected boolean hasSetter() {
		return setter != null;
	}

}


