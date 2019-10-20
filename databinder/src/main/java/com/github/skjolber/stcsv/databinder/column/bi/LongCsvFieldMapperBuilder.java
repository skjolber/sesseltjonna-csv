package com.github.skjolber.stcsv.databinder.column.bi;

import java.util.function.ObjLongConsumer;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.databinder.builder.AbstractCsvMappingBuilder;
import com.github.skjolber.stcsv.databinder.builder.AbstractTypedCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.databinder.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.databinder.projection.BiConsumerProjection;
import com.github.skjolber.stcsv.databinder.projection.ValueProjection;

public class LongCsvFieldMapperBuilder<T, B extends AbstractCsvMappingBuilder<T, ?>> extends AbstractTypedCsvFieldMapperBuilder<T, B> {

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
	
	protected ValueProjection getProjection(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException {
		if(consumer != null) {
			return new BiConsumerProjection(new LongCsvColumnValueConsumer<>(consumer), index);
		}
		return super.getProjection(index, proxy);
	}

}


