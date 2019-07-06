package com.github.skjolber.stcsv.builder;

import com.github.skjolber.stcsv.AbstractCsvReader;

public abstract class AbstractCsvBuilder<B>  {

	protected static boolean isSafeCharDelimiter(char c) {
		return !Character.isLowSurrogate(c);
	}
	
	protected char divider = ',';
	protected char quoteCharacter = '"';
	protected char escapeCharacter = '"';
	
	protected boolean skipComments = false;
	protected boolean skipEmptyLines = false;
	protected int bufferLength = AbstractCsvReader.DEFAULT_RANGE_LENGTH;

	@SuppressWarnings("unchecked")
	public B skipEmptyLines() {
		this.skipEmptyLines = true;
		
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B skipComments() {
		this.skipComments = true;
		
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B bufferLength(int length) {
		this.bufferLength = length;
		
		return (B) this;
	}
	
	@SuppressWarnings("unchecked")
	public B divider(char c) {
		if(!isSafeCharDelimiter(c) || c == '\n') {
			throw new CsvBuilderException("Cannot use character '" + c + "' as divider");
		}
		this.divider = c;
		
		return (B) this;
	}
	
	@SuppressWarnings("unchecked")
	public B quoteCharacter(char c) {
		this.quoteCharacter = c;
		
		return (B) this;
	}
	
	@SuppressWarnings("unchecked")
	public B escapeCharacter(char c) {
		this.escapeCharacter = c;
		
		return (B) this;
	}

	public char getEscapeCharacter() {
		return escapeCharacter;
	}
	
	public char getQuoteCharacter() {
		return quoteCharacter;
	}
	
	public char getDivider() {
		return divider;
	}

	public int getBufferLength() {
		return bufferLength;
	}
}
