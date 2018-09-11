package com.github.skjolber.stcsv.builder;

import java.util.function.ObjDoubleConsumer;

import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.DoubleCsvColumnValueConsumer;

public class DoubleCsvFieldMapperBuilder<T> extends AbstractCsvFieldMapperBuilder<T> {

	protected ObjDoubleConsumer<T> consumer;
	protected ObjDoubleConsumer<T> setter;

	public DoubleCsvFieldMapperBuilder(CsvMappingBuilder<T> parent, String name) {
		super(parent, name);
	}

	public DoubleCsvFieldMapperBuilder<T> consumer(ObjDoubleConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}
	

	public DoubleCsvFieldMapperBuilder<T> setter(ObjDoubleConsumer<T> setter) {
		this.setter = setter;
		
		return this;
	}	
	
	public DoubleCsvFieldMapperBuilder<T> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public DoubleCsvFieldMapperBuilder<T> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	
	public DoubleCsvFieldMapperBuilder<T> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public DoubleCsvFieldMapperBuilder<T> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public DoubleCsvFieldMapperBuilder<T> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
	}

	@Override
	public CsvColumnValueConsumer<T> getValueConsumer() {
		if(consumer != null) {
			return new DoubleCsvColumnValueConsumer<>(consumer);	
		}
		return null;
	}

	@Override
	protected Class<?> getColumnClass() {
		return double.class;
	}

	@Override
	protected void invokeSetter(T value) {
		setter.accept(value, 0d);
	}

	@Override
	protected boolean hasSetter() {
		return setter != null;
	}

}


