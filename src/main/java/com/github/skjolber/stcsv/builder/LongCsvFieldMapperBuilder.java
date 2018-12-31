package com.github.skjolber.stcsv.builder;

import java.util.function.ObjLongConsumer;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.column.bi.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.bi.LongCsvColumnValueConsumer;

public class LongCsvFieldMapperBuilder<T, D extends AbstractCsvMappingBuilder<T, D>> extends AbstractCsvFieldMapperBuilder<T, D> {

	protected ObjLongConsumer<T> consumer;
	protected ObjLongConsumer<T> setter;

	public LongCsvFieldMapperBuilder(D parent, String name) {
		super(parent, name);
	}

	public LongCsvFieldMapperBuilder<T, D> consumer(ObjLongConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}

	public LongCsvFieldMapperBuilder<T, D> setter(ObjLongConsumer<T> setter) {
		this.setter = setter;
		
		return this;
	}
	
	public LongCsvFieldMapperBuilder<T, D> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public LongCsvFieldMapperBuilder<T, D> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public LongCsvFieldMapperBuilder<T, D> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public LongCsvFieldMapperBuilder<T, D> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public LongCsvFieldMapperBuilder<T, D> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
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
	
	protected void buildProjection(AbstractColumn column, SetterProjectionHelper<T> proxy) {
		if(consumer != null) {
			column.setBiConsumer(new LongCsvColumnValueConsumer<>(consumer));
		} else {
			super.buildProjection(column, proxy);
		}
	}

}


