package com.github.skjolber.stcsv.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.AbstractCsvReader;

public abstract class AbstractCsvMappingBuilder<T, B extends AbstractCsvMappingBuilder<T, ?>>  {

	protected static final boolean byteBuddy;
	
	static {
		boolean present;
		try {
			Class.forName("net.bytebuddy.ByteBuddy");
			
			present = true;
		} catch(Exception e) {
			present = false;
		}
		byteBuddy = present;
		
	}

	
	protected static boolean isSafeByteUTF8Delimiter(char c) {
		//https://en.wikipedia.org/wiki/UTF-8#Description
		// all char 2, 3 and 4 are negative numbers in UTF-8
		return c >= 0;
	}
	
	protected static boolean isSafeCharDelimiter(char c) {
		return !Character.isLowSurrogate(c);
	}
	
	protected Class<T> target;
	protected char divider = ',';
	protected char quoteCharacter = '"';
	protected char escapeCharacter = '"';
	
	protected boolean skipComments = false;
	protected boolean skipEmptyLines = false;
	protected boolean skippableFieldsWithoutLinebreaks = false;
	protected int bufferLength = AbstractCsvReader.DEFAULT_RANGE_LENGTH;
	
	protected ClassLoader classLoader;

	protected List<AbstractCsvFieldMapperBuilder<T, ? extends AbstractCsvMappingBuilder<T, ?>>> fields = new ArrayList<>();

	public AbstractCsvMappingBuilder(Class<T> cls) {
		this.target = cls;
	}
	
	public B skipEmptyLines() {
		this.skipEmptyLines = true;
		
		return (B) this;
	}

	public B skipComments() {
		this.skipComments = true;
		
		return (B) this;
	}

	/**
	 * 
	 * All fields (including any ignored columns) are without linebreaks.
	 * 
	 * @return this
	 */

	public B skippableFieldsWithoutLinebreaks() {
		this.skippableFieldsWithoutLinebreaks = true;
		
		return (B) this;
	}
	
	public B classLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		
		return (B) this;
	}
	
	public B bufferLength(int length) {
		this.bufferLength = length;
		
		return (B) this;
	}
	
	public B divider(char c) {
		if(!isSafeCharDelimiter(c) || c == '\n') {
			throw new IllegalArgumentException("Cannot use character '" + c + "' as divider");
		}
		this.divider = c;
		
		return (B) this;
	}
	
	public B quoteCharacter(char c) {
		this.quoteCharacter = c;
		
		return (B) this;
	}
	
	public B escapeCharacter(char c) {
		this.escapeCharacter = c;
		
		return (B) this;
	}

	protected List<AbstractColumn> toColumns() throws CsvBuilderException {
		List<AbstractColumn> columns = new ArrayList<>(fields.size());
		Set<String> fieldNames = new HashSet<>(fields.size() * 2);

		SetterProjectionHelper<T> proxy;
		if(byteBuddy) {
			proxy = new ByteBuddySetterProjectionHelper<T>(target);
		} else {
			proxy = new SetterProjectionHelper<T>(target);
		}
		
		for (int i = 0; i < fields.size(); i++) {
			AbstractCsvFieldMapperBuilder<T, ?> builder = fields.get(i);
			
			String name = builder.getName();
			if(fieldNames.contains(name)) {
				throw new IllegalArgumentException("Duplicate field '" + name + "'");
			}
			fieldNames.add(name);
			
			if(builder.isLinebreaks() && !builder.isQuoted()) {
				throw new RuntimeException();
			}

			columns.add(builder.build(i, proxy));
		}
		return columns;
	}

	protected ClassLoader getDefaultClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	
	public char getEscapeCharacter() {
		return escapeCharacter;
	}
	
	public char getQuoteCharacter() {
		return quoteCharacter;
	}

	protected B field(AbstractCsvFieldMapperBuilder<T, ? extends AbstractCsvMappingBuilder<T, ?>> field) {
		this.fields.add(field);
		
		return (B) this;
	}

	public Class<T> getTarget() {
		return target;
	}
	
}
