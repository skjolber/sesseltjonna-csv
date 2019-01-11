package com.github.skjolber.stcsv.column.bi;

import java.util.function.ObjLongConsumer;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.builder.AbstractCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.builder.AbstractCsvMappingBuilder;
import com.github.skjolber.stcsv.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.projection.BiConsumerProjection;

public class LongCsvFieldMapperBuilder<T, B extends AbstractCsvMappingBuilder<T, ?>> extends AbstractCsvFieldMapperBuilder<T, B> {

	protected ObjLongConsumer<T> consumer;
	protected ObjLongConsumer<T> setter;

	public LongCsvFieldMapperBuilder(B parent, String name) {
		super(parent, name);
	}

	public LongCsvFieldMapperBuilder<T, B> consumer(ObjLongConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}

	public LongCsvFieldMapperBuilder<T, B> setter(ObjLongConsumer<T> setter) {
		this.setter = setter;
		
		return this;
	}
	
	public LongCsvFieldMapperBuilder<T, B> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public LongCsvFieldMapperBuilder<T, B> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public LongCsvFieldMapperBuilder<T, B> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public LongCsvFieldMapperBuilder<T, B> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public LongCsvFieldMapperBuilder<T, B> trimLeadingWhitespaces() {
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
	
	protected void buildProjection(AbstractColumn column, SetterProjectionHelper<T> proxy, int index) {
		if(consumer != null) {
			column.setProjection(new BiConsumerProjection(new LongCsvColumnValueConsumer<>(consumer), index));
		} else {
			super.buildProjection(column, proxy, index);
		}
	}

}


