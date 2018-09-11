package com.github.skjolber.stcsv.builder;

import java.util.function.BiConsumer;

import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.StringCsvColumnValueConsumer;

public class StringCsvFieldMapperBuilder<T> extends AbstractCsvFieldMapperBuilder<T> {

	protected BiConsumer<T, String> consumer;
	protected BiConsumer<T, String> setter;

	public StringCsvFieldMapperBuilder(CsvMappingBuilder<T> parent, String name) {
		super(parent, name);
	}

	public StringCsvFieldMapperBuilder<T> consumer(BiConsumer<T, String> consumer) {
		this.consumer = consumer;
		
		return this;
	}

	public StringCsvFieldMapperBuilder<T> setter(BiConsumer<T, String> setter) {
		this.setter = setter;
		
		return this;
	}
	
	public StringCsvFieldMapperBuilder<T> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public StringCsvFieldMapperBuilder<T> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public StringCsvFieldMapperBuilder<T> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public StringCsvFieldMapperBuilder<T> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public StringCsvFieldMapperBuilder<T> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
	}
	
	@Override
	public CsvColumnValueConsumer<T> getValueConsumer() {
		if(consumer != null) {
			return new StringCsvColumnValueConsumer<>(consumer);
		}
		return null;
	}

	@Override
	protected Class<?> getColumnClass() {
		return String.class;
	}

	@Override
	protected void invokeSetter(T value) {
		setter.accept(value, null);
	}

	@Override
	protected boolean hasSetter() {
		return setter != null;
	}

}

