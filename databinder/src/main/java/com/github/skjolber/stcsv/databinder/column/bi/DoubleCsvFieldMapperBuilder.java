package com.github.skjolber.stcsv.databinder.column.bi;

import java.util.function.ObjDoubleConsumer;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.databinder.builder.AbstractCsvMappingBuilder;
import com.github.skjolber.stcsv.databinder.builder.AbstractTypedCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.databinder.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.databinder.projection.BiConsumerProjection;
import com.github.skjolber.stcsv.databinder.projection.ValueProjection;

public class DoubleCsvFieldMapperBuilder<T, D extends AbstractCsvMappingBuilder<T, ?>> extends AbstractTypedCsvFieldMapperBuilder<T, D> {

	protected ObjDoubleConsumer<T> consumer;
	protected ObjDoubleConsumer<T> setter;

	public DoubleCsvFieldMapperBuilder(D parent, String name) {
		super(parent, name);
	}

	public DoubleCsvFieldMapperBuilder<T, D> consumer(ObjDoubleConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}
	
	public DoubleCsvFieldMapperBuilder<T, D> setter(ObjDoubleConsumer<T> setter) {
		this.setter = setter;
		
		return this;
	}	
	
	public DoubleCsvFieldMapperBuilder<T, D> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public DoubleCsvFieldMapperBuilder<T, D> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	
	public DoubleCsvFieldMapperBuilder<T, D> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public DoubleCsvFieldMapperBuilder<T, D> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public DoubleCsvFieldMapperBuilder<T, D> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
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

	protected ValueProjection getProjection(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException {
		if(consumer != null) {
			return new BiConsumerProjection(new DoubleCsvColumnValueConsumer<>(consumer), index);
		}
		return super.getProjection(index, proxy);
	}

}


