package com.github.skjolber.stcsv.builder;

import com.github.skjolber.stcsv.column.BooleanCsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.ObjBooleanConsumer;

public class BooleanCsvFieldMapperBuilder<T> extends AbstractCsvFieldMapperBuilder<T> {

	protected ObjBooleanConsumer<T> consumer;
	protected ObjBooleanConsumer<T> setter;

	public BooleanCsvFieldMapperBuilder(CsvMappingBuilder<T> parent, String name) {
		super(parent, name);
	}

	public BooleanCsvFieldMapperBuilder<T> consumer(ObjBooleanConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}
	
	public BooleanCsvFieldMapperBuilder<T> setter(ObjBooleanConsumer<T> setter) {
		this.setter = setter;
		
		return this;
	}
	
	protected ObjBooleanConsumer<T> getSetter() {
		return consumer;
	}
	
	public BooleanCsvFieldMapperBuilder<T> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public BooleanCsvFieldMapperBuilder<T> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public BooleanCsvFieldMapperBuilder<T> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public BooleanCsvFieldMapperBuilder<T> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public BooleanCsvFieldMapperBuilder<T> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
	}

	@Override
	public CsvColumnValueConsumer<T> getValueConsumer() {
		if(consumer != null) {
			return new BooleanCsvColumnValueConsumer<>(consumer);
		}
		return null;
	}

	@Override
	protected Class<?> getColumnClass() {
		return boolean.class;
	}

	@Override
	protected void invokeSetter(T value) {
		setter.accept(value, true);
	}

	@Override
	protected boolean hasSetter() {
		return setter != null;
	}
}


