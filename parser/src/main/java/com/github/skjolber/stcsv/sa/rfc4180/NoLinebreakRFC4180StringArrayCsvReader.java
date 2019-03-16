package com.github.skjolber.stcsv.sa.rfc4180;

import java.io.IOException;
import java.io.Reader;

import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.sa.StringArrayCsvReader;

public final class NoLinebreakRFC4180StringArrayCsvReader extends StringArrayCsvReader {
	
	protected final String[] value;
	protected final int lastIndex;

	public NoLinebreakRFC4180StringArrayCsvReader(Reader reader, int columns) {
		super(reader, 65536);
		
		this.value = new String[columns];
		this.lastIndex = columns - 1;
	}

	public NoLinebreakRFC4180StringArrayCsvReader(Reader reader, char[] current, int offset, int length, int columns) {
		super(reader, current, offset, length);

		this.value = new String[columns];
		this.lastIndex = columns - 1;
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
				if (current[currentOffset] != '"') {
					if (current[currentOffset] != ',') {
						start = currentOffset;
	
						do {
							++currentOffset;
						} while (current[currentOffset] != ',');
	
						value[i] = new String(current, start, currentOffset - start);
					} else {
						value[i] = null;
					}
				} else {
					start = currentOffset + 1;
	
					quoted : while (true) {
						while (current[++currentOffset] != '"');

						if (current[currentOffset] == '"') {
							if (current[currentOffset + 1] != '"') {
								if (currentOffset > start) {
									value[i] = new String(current, start, currentOffset - start);
								} else {
									value[i] = null;
								}
	
								do {
									++currentOffset;
								} while (current[currentOffset] != ',');
								
								break quoted;
							}
	
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
							++currentOffset;
							++start;
						}
					}
				}
				++currentOffset;
			}
			
			// last column
			if (current[currentOffset] != '"') {
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
				start = currentOffset + 1;

				quoted : while (true) {
					while (current[++currentOffset] != '"');
					
					if (current[currentOffset] == '"') {
						if (current[currentOffset + 1] != '"') {
							if (currentOffset > start) {
								value[lastIndex] = new String(current, start, currentOffset - start);
							} else {
								value[lastIndex] = null;
							}

							do {
								++currentOffset;
							} while (current[currentOffset] != '\n');
							
							break quoted;
						}

						System.arraycopy(current, start, current, start + 1, currentOffset - start);
						++currentOffset;
						++start;
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