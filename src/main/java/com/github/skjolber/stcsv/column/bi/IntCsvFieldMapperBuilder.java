package com.github.skjolber.stcsv.column.bi;

import java.util.function.ObjIntConsumer;

import com.github.skjolber.stcsv.builder.AbstractCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.builder.AbstractCsvMappingBuilder;
import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.projection.BiConsumerProjection;
import com.github.skjolber.stcsv.projection.ValueProjection;

public class IntCsvFieldMapperBuilder<T, D extends AbstractCsvMappingBuilder<T, ?>> extends AbstractCsvFieldMapperBuilder<T, D> {

	protected ObjIntConsumer<T> consumer;
	protected ObjIntConsumer<T> setter;

	public IntCsvFieldMapperBuilder(D parent, String name) {
		super(parent, name);
	}

	public IntCsvFieldMapperBuilder<T, D> consumer(ObjIntConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}
	
	public IntCsvFieldMapperBuilder<T, D> setter(ObjIntConsumer<T> setter) {
		this.setter = setter;
		
		return this;
	}	
	
	public IntCsvFieldMapperBuilder<T, D> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public IntCsvFieldMapperBuilder<T, D> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public IntCsvFieldMapperBuilder<T, D> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public IntCsvFieldMapperBuilder<T, D> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public IntCsvFieldMapperBuilder<T, D> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
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

	protected ValueProjection getProjection(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException {
		if(consumer != null) {
			return new BiConsumerProjection(new IntCsvColumnValueConsumer<>(consumer), index);
		}
		return super.getProjection(index, proxy);
	}

}


