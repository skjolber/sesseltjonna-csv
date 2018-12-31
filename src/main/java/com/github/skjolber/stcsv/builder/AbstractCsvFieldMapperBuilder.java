package com.github.skjolber.stcsv.builder;

import java.lang.reflect.Method;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.NoLineBreakQuotedColumn;
import com.github.skjolber.stcsv.PlainColumn;
import com.github.skjolber.stcsv.PlainFixedColumn;
import com.github.skjolber.stcsv.QuotedColumn;
import com.github.skjolber.stcsv.QuotedFixedColumn;
import com.github.skjolber.stcsv.column.bi.CsvColumnValueConsumer;

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

	protected Integer getFixedSize() {
		return fixedSize;
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

	public AbstractColumn build(int index, SetterProjectionHelper<T> proxy) {
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
		
		buildProjection(column, proxy);
		
		return column;
	}

	protected void buildProjection(AbstractColumn column, SetterProjectionHelper<T> proxy) {
		Method method = proxy.toMethod(this);
		
		column.setSetter(method.getName(), method.getParameterTypes()[0]);
	}
	
	protected Class<?> getColumnClass() {
		throw new RuntimeException();
	}
	
	protected void invokeSetter(T value) {
		throw new RuntimeException("No setter for column '" + name + "'");
	}

	protected boolean hasSetter() {
		return false;
	}
	
	protected boolean hasBiConsumer() {
		return false;
	}

	protected boolean hasTriConsumer() {
		return false;
	}

}


