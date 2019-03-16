package com.github.skjolber.stcsv.sa;

import java.io.IOException;
import java.io.Reader;

import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.builder.StringArrayCsvReaderBuilder;

public class DefaultStringArrayCsvReader extends StringArrayCsvReader {
	
	public static StringArrayCsvReaderBuilder builder() {
		return new StringArrayCsvReaderBuilder();
	}
	
	protected final String[] value;
	protected final int lastIndex;
	
	protected final char divider;
	protected final char quoteCharacter;
	protected final char escapeCharacter;
	
	protected final char maxCharacter;
	
	public DefaultStringArrayCsvReader(Reader reader, int columns, char quoteCharacter, char escapeCharacter, char divider) {
		super(reader, 65536);
		
		this.value = new String[columns];
		this.lastIndex = columns - 1;
		
		this.quoteCharacter = quoteCharacter;
		this.escapeCharacter = escapeCharacter;
		this.divider = divider;
		this.maxCharacter = (char) Math.max(quoteCharacter, Math.max(escapeCharacter, '\n'));
	}

	public DefaultStringArrayCsvReader(Reader reader, char[] current, int offset, int length, int columns, char quoteCharacter, char escapeCharacter, char divider) {
		super(reader, current, offset, length);

		this.value = new String[columns];
		this.lastIndex = columns - 1;
		
		this.quoteCharacter = quoteCharacter;
		this.escapeCharacter = escapeCharacter;
		this.divider = divider;
		this.maxCharacter = (char) Math.max(quoteCharacter, escapeCharacter);
	}	
	
	public String[] next() throws IOException {
		int currentOffset = super.currentOffset;
		if (currentOffset >= super.currentRange) {
			if (this.fill() == 0) {
				return null;
			}

			currentOffset = 0;
		}

		char[] current = super.current;

		try {
			String[] value = this.value;

			int start;

			for(int i = 0; i < lastIndex; i++) {
				if (current[currentOffset] != quoteCharacter) {
					if (current[currentOffset] != divider) {
						start = currentOffset;
	
						do {
							++currentOffset;
						} while (current[currentOffset] != divider);
	
						value[i] = new String(current, start, currentOffset - start);
					} else {
						value[i] = null;
					}
				} else {
					int rangeIndex = this.getCurrentRange();
					start = currentOffset + 1;
	
					quoted : while (true) {
						while (current[++currentOffset] > maxCharacter);

						if (current[currentOffset] == quoteCharacter) {
							if (currentOffset > start) {
								value[i] = new String(current, start, currentOffset - start);
							} else {
								value[i] = null;
							}

							do {
								++currentOffset;
							} while (current[currentOffset] != divider);
							break quoted;
						} else 	if (current[currentOffset] == escapeCharacter) {
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
							++currentOffset;
							++start;
						} else if (current[currentOffset] == '\n' && currentOffset == rangeIndex) {
							currentOffset -= start;
							if ((rangeIndex = this.fill(currentOffset)) <= currentOffset) {
								throw new CsvException("Illegal value in column " + i);
							}
	
							start = 0;
						}
					}
				}
				++currentOffset;
			}
			
			// last column
			if (current[currentOffset] != quoteCharacter) {
				if (current[currentOffset] != '\n') {
					start = currentOffset;

					do {
						++currentOffset;
					} while (current[currentOffset] != '\n');

					if(current[currentOffset - 1] == '\r') { // check for linefeed
						value[lastIndex] = new String(current, start, currentOffset - start - 1);
					} else {
						value[lastIndex] = new String(current, start, currentOffset - start);
					}
				} else {
					value[lastIndex] = null;
				}
			} else {
				int rangeIndex = this.getCurrentRange();
				start = currentOffset + 1;

				quoted : while (true) {
					while (current[++currentOffset] > maxCharacter);

					if (current[currentOffset] == quoteCharacter) {
						if (currentOffset > start) {
							value[lastIndex] = new String(current, start, currentOffset - start);
						} else {
							value[lastIndex] = null;
						}

						do {
							++currentOffset;
						} while (current[currentOffset] != '\n');
						break quoted;
					} else 	if (current[currentOffset] == escapeCharacter) {
						System.arraycopy(current, start, current, start + 1, currentOffset - start);
						++currentOffset;
						++start;
					} else if (current[currentOffset] == '\n' && currentOffset == rangeIndex) {
						currentOffset -= start;
						if ((rangeIndex = this.fill(currentOffset)) <= currentOffset) {
							throw new CsvException("Illegal value in column " + lastIndex);
						}

						start = 0;
					}					
				}
			}
			++currentOffset;			

			super.currentOffset = currentOffset;

			return value;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new CsvException(e);
		}
	}
}