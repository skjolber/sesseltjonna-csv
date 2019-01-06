package com.github.skjolber.stcsv.column.bi;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.builder.AbstractCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.builder.AbstractCsvMappingBuilder;
import com.github.skjolber.stcsv.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.column.tri.StringCsvColumnValueTriConsumer;

public class BooleanCsvFieldMapperBuilder<T, D extends AbstractCsvMappingBuilder<T, D>> extends AbstractCsvFieldMapperBuilder<T, D> {

	protected ObjBooleanConsumer<T> consumer;
	protected ObjBooleanConsumer<T> setter;

	public BooleanCsvFieldMapperBuilder(D parent, String name) {
		super(parent, name);
	}

	public BooleanCsvFieldMapperBuilder<T, D> consumer(ObjBooleanConsumer<T> consumer) {
		this.consumer = consumer;
		
		return this;
	}
	
	public BooleanCsvFieldMapperBuilder<T, D> setter(ObjBooleanConsumer<T> setter) {
		this.setter = setter;
		
		return this;
	}
	
	protected ObjBooleanConsumer<T> getSetter() {
		return consumer;
	}
	
	public BooleanCsvFieldMapperBuilder<T, D> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public BooleanCsvFieldMapperBuilder<T, D> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public BooleanCsvFieldMapperBuilder<T, D> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public BooleanCsvFieldMapperBuilder<T, D> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public BooleanCsvFieldMapperBuilder<T, D> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
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
	
	@Override
	protected void buildProjection(AbstractColumn column, SetterProjectionHelper<T> proxy) {
		if(consumer != null) {
			column.setBiConsumer(new BooleanCsvColumnValueConsumer<>(consumer));
		} else {
			super.buildProjection(column, proxy);
		}
	}

}


