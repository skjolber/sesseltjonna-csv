package com.github.skjolber.stcsv.builder;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.NoLineBreakQuotedColumn;
import com.github.skjolber.stcsv.PlainColumn;
import com.github.skjolber.stcsv.PlainFixedColumn;
import com.github.skjolber.stcsv.QuotedColumn;
import com.github.skjolber.stcsv.QuotedFixedColumn;
import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;

public abstract class AbstractCsvFieldMapperBuilder<T> {

	protected final String name;
	protected boolean optional;
	protected Integer fixedSize;
	protected boolean quoted;
	protected boolean linebreaks;

	protected boolean trimTrailingWhitespaces = false;
	protected boolean trimLeadingWhitespaces = false;

	protected CsvMappingBuilder<T> parent;

	public AbstractCsvFieldMapperBuilder(CsvMappingBuilder<T> parent, String name) {
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

	public CsvMappingBuilder<T> optional() {
		this.optional = true;
		parent.field(this);
		
		return parent;
	}
	
	public AbstractCsvFieldMapperBuilder<T> fixedSize(int fixedSize) {
		this.fixedSize = fixedSize;
		
		return this;
	}

	public CsvMappingBuilder<T> required() {
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
	
	public AbstractCsvFieldMapperBuilder<T> quoted() {
		quoted = true;
		linebreaks = true;
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public AbstractCsvFieldMapperBuilder<T> quotedWithoutLinebreaks() {
		quoted = true;
		linebreaks = false;
		
		return this;
	}

	public AbstractCsvFieldMapperBuilder<T> trimTrailingWhitespaces() {
		this.trimTrailingWhitespaces = true;
		
		return this;
	}

	public AbstractCsvFieldMapperBuilder<T> trimLeadingWhitespaces() {
		this.trimLeadingWhitespaces = true;
		
		return this;
	}

	protected boolean isTrimLeadingWhitespaces() {
		return trimLeadingWhitespaces;
	}
	
	protected boolean isTrimTrailingWhitespaces() {
		return trimTrailingWhitespaces;
	}
	
	public abstract CsvColumnValueConsumer<T> getValueConsumer();

	public AbstractColumn build(int index) {
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
		return column;
	}

	protected String getSetterName() {
		return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	protected String getNormalizedSetterName() {
		
		StringBuilder builder = new StringBuilder("set");
		
		boolean high = true;
		for(int i = 0; i < name.length(); i++) {
			if(high) {
				builder.append(Character.toUpperCase(name.charAt(i)));
				
				high = false;
			} else if(name.charAt(i) == '_') {
				high = true;
			} else {
				builder.append(name.charAt(i));
			}
		}
		
		return builder.toString();
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
}


