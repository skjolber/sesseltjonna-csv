package com.github.skjolber.stcsv.builder;

import java.lang.reflect.Method;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.NoLineBreakQuotedColumn;
import com.github.skjolber.stcsv.PlainColumn;
import com.github.skjolber.stcsv.PlainFixedColumn;
import com.github.skjolber.stcsv.QuotedColumn;
import com.github.skjolber.stcsv.QuotedFixedColumn;
import com.github.skjolber.stcsv.projection.SetterValueProjection;
import com.github.skjolber.stcsv.projection.ValueProjection;

public abstract class AbstractCsvFieldMapperBuilder<T, B extends AbstractCsvMappingBuilder<T, ?>> {

	protected final String name;
	protected boolean optional;
	protected Integer fixedSize;
	protected boolean quoted;
	protected boolean linebreaks;

	protected boolean trimTrailingWhitespaces = false;
	protected boolean trimLeadingWhitespaces = false;

	protected B parent;

	public AbstractCsvFieldMapperBuilder(B parent, String name) {
		super();
		this.parent = parent;
		this.name = name;
	}

	protected String getName() {
		return name;
	}
	
	protected boolean isOptional() {
		return optional;
	}

	public B optional() {
		this.optional = true;
		parent.field(this);
		
		return parent;
	}
	
	public AbstractCsvFieldMapperBuilder<T, B> fixedSize(int fixedSize) {
		this.fixedSize = fixedSize;
		
		return this;
	}

	public B required() {
		this.optional = false;

		parent.field(this);

		return parent;
	}

	protected boolean isQuoted() {
		return quoted;
	}

	public Boolean isLinebreaks() {
		return linebreaks;
	}
	
	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public AbstractCsvFieldMapperBuilder<T, B> quoted() {
		quoted = true;
		linebreaks = true;
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public AbstractCsvFieldMapperBuilder<T, B> quotedWithoutLinebreaks() {
		quoted = true;
		linebreaks = false;
		
		return this;
	}

	public AbstractCsvFieldMapperBuilder<T, B> trimTrailingWhitespaces() {
		this.trimTrailingWhitespaces = true;
		
		return this;
	}

	public AbstractCsvFieldMapperBuilder<T, B> trimLeadingWhitespaces() {
		this.trimLeadingWhitespaces = true;
		
		return this;
	}

	protected boolean isTrimLeadingWhitespaces() {
		return trimLeadingWhitespaces;
	}
	
	protected boolean isTrimTrailingWhitespaces() {
		return trimTrailingWhitespaces;
	}

	public AbstractColumn build(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException {
		AbstractColumn column;
		if(quoted) {
			if(fixedSize != null) {
				column = new QuotedFixedColumn(name, index, parent.getQuoteCharacter(), parent.getEscapeCharacter(), optional, trimTrailingWhitespaces, trimLeadingWhitespaces, fixedSize);
			} else if(!linebreaks) {
				column = new NoLineBreakQuotedColumn(name, index, parent.getQuoteCharacter(), parent.getEscapeCharacter(), optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
			} else {
				column = new QuotedColumn(name, index, parent.getQuoteCharacter(), parent.getEscapeCharacter(), optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
			}
		} else {
			if(fixedSize != null) {
				column = new PlainFixedColumn(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces, fixedSize);
			} else {
				column = new PlainColumn(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
			}
		}
		
		column.setProjection(getProjection(index, proxy));
		
		return column;
	}

	protected ValueProjection getProjection(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException {
		Method method = proxy.toMethod(this);
		
		return SetterValueProjection.newInstance(method.getParameterTypes()[0], method.getName(), CsvMapper.getInternalName(parent.getTarget())); 
	}
	
	protected abstract Class<?> getColumnClass();
	
	protected abstract void invokeSetter(T value);

	protected abstract boolean hasSetter();

}


