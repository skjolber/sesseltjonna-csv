package com.github.skjolber.stcsv.column.bi;

import java.util.function.ObjDoubleConsumer;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.builder.AbstractCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.builder.AbstractCsvMappingBuilder;
import com.github.skjolber.stcsv.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.projection.BiConsumerProjection;

public class DoubleCsvFieldMapperBuilder<T, D extends AbstractCsvMappingBuilder<T, D>> extends AbstractCsvFieldMapperBuilder<T, D> {

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

	protected void buildProjection(AbstractColumn column, SetterProjectionHelper<T> proxy, int index) {
		if(consumer != null) {
			column.setProjection(new BiConsumerProjection(new DoubleCsvColumnValueConsumer<>(consumer), index));
		} else {
			super.buildProjection(column, proxy, index);
		}
	}

}

