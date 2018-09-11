package com.github.skjolber.stcsv.builder;

import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;

public class CsvFieldMapperBuilder<T> extends AbstractCsvFieldMapperBuilder<T> {

	protected CsvColumnValueConsumer<T> consumer;

	public CsvFieldMapperBuilder(CsvMappingBuilder<T> parent, String name) {
		super(parent, name);
	}

	public CsvFieldMapperBuilder<T> consumer(CsvColumnValueConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}
	
	public CsvColumnValueConsumer<T> getValueConsumer() {
		return consumer;
	}

	public CsvFieldMapperBuilder<T> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public CsvFieldMapperBuilder<T> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public CsvFieldMapperBuilder<T> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public CsvFieldMapperBuilder<T> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public CsvFieldMapperBuilder<T> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
	}

}


