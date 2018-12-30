package com.github.skjolber.stcsv.builder;

import com.github.skjolber.stcsv.column.bi.CsvColumnValueConsumer;

public class CsvFieldMapperBuilder<T, D extends AbstractCsvMappingBuilder> extends AbstractCsvFieldMapperBuilder<T, D> {

	protected CsvColumnValueConsumer<T> consumer;

	public CsvFieldMapperBuilder(D parent, String name) {
		super(parent, name);
	}

	public CsvFieldMapperBuilder<T, D> consumer(CsvColumnValueConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}
	
	public CsvColumnValueConsumer<T> getBiConsumer() {
		return consumer;
	}

	public CsvFieldMapperBuilder<T, D> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public CsvFieldMapperBuilder<T, D> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public CsvFieldMapperBuilder<T, D> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public CsvFieldMapperBuilder<T, D> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public CsvFieldMapperBuilder<T, D> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
	}

}


