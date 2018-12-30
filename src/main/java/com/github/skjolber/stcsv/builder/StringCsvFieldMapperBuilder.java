package com.github.skjolber.stcsv.builder;

import java.util.function.BiConsumer;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.column.bi.StringCsvColumnValueConsumer;

public class StringCsvFieldMapperBuilder<T, B extends AbstractCsvMappingBuilder> extends AbstractCsvFieldMapperBuilder<T, B> {

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

	public AbstractColumn build(int index) {
		AbstractColumn build = super.build(index);
		if(consumer != null) {
			build.setBiConsumer(new StringCsvColumnValueConsumer<>(consumer));
		}
		return build;
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
	
	protected boolean hasBiConsumer() {
		return consumer != null;
	}
	
}

