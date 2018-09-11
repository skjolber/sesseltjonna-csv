package com.github.skjolber.stcsv.builder;

import java.util.function.ObjLongConsumer;

import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.LongCsvColumnValueConsumer;

public class LongCsvFieldMapperBuilder<T> extends AbstractCsvFieldMapperBuilder<T> {

	protected ObjLongConsumer<T> consumer;
	protected ObjLongConsumer<T> setter;

	public LongCsvFieldMapperBuilder(CsvMappingBuilder<T> parent, String name) {
		super(parent, name);
	}

	public LongCsvFieldMapperBuilder<T> consumer(ObjLongConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}

	public LongCsvFieldMapperBuilder<T> setter(ObjLongConsumer<T> setter) {
		this.setter = setter;
		
		return this;
	}
	
	public LongCsvFieldMapperBuilder<T> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public LongCsvFieldMapperBuilder<T> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public LongCsvFieldMapperBuilder<T> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public LongCsvFieldMapperBuilder<T> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public LongCsvFieldMapperBuilder<T> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
	}

	@Override
	public CsvColumnValueConsumer<T> getValueConsumer() {
		if(consumer != null) {
			return new LongCsvColumnValueConsumer<>(consumer);
		}
		return null;
	}
	
	@Override
	protected Class<?> getColumnClass() {
		return long.class;
	}

	@Override
	protected void invokeSetter(T value) {
		setter.accept(value, 0L);
	}
	
	@Override
	protected boolean hasSetter() {
		return setter != null;
	}
	
}


