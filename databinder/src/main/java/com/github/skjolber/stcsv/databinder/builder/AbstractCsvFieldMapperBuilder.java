package com.github.skjolber.stcsv.databinder.builder;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.databinder.AbstractColumn;
import com.github.skjolber.stcsv.databinder.NoLineBreakQuotedColumn;
import com.github.skjolber.stcsv.databinder.PlainColumn;
import com.github.skjolber.stcsv.databinder.PlainFixedColumn;
import com.github.skjolber.stcsv.databinder.QuotedColumn;
import com.github.skjolber.stcsv.databinder.QuotedFixedColumn;
import com.github.skjolber.stcsv.databinder.projection.ValueProjection;

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

	protected abstract ValueProjection getProjection(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException;

}


