package com.github.skjolber.stcsv.column.bi;

import java.util.function.BiConsumer;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.builder.AbstractCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.builder.AbstractCsvMappingBuilder;
import com.github.skjolber.stcsv.builder.SetterProjectionHelper;

public class StringCsvFieldMapperBuilder<T, B extends AbstractCsvMappingBuilder<T, ?>> extends AbstractCsvFieldMapperBuilder<T, B> {

	protected BiConsumer<T, String> consumer;
	protected BiConsumer<T, String> setter;

	public StringCsvFieldMapperBuilder(B parent, String name) {
		super(parent, name);
	}

	public StringCsvFieldMapperBuilder<T, B> consumer(BiConsumer<T, String> consumer) {
		this.consumer = consumer;
		
		return this;
	}

	public StringCsvFieldMapperBuilder<T, B> setter(BiConsumer<T, String> setter) {
		this.setter = setter;
		
		return this;
	}
	
	public StringCsvFieldMapperBuilder<T, B> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public StringCsvFieldMapperBuilder<T, B> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public StringCsvFieldMapperBuilder<T, B> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public StringCsvFieldMapperBuilder<T, B> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public StringCsvFieldMapperBuilder<T, B> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
	}

	protected void buildProjection(AbstractColumn column, SetterProjectionHelper<T> proxy) {
		if(consumer != null) {
			column.setBiConsumer(new StringCsvColumnValueConsumer<>(consumer));
		} else {
			super.buildProjection(column, proxy);
		}
	}
	
	@Override
	protected Class<?> getColumnClass() {
		return String.class;
	}

	@Override
	protected void invokeSetter(T value) {
		setter.accept(value, null);
	}

	@Override
	protected boolean hasSetter() {
		return setter != null;
	}
	
}

