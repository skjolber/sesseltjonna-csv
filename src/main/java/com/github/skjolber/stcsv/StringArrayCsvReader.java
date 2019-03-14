package com.github.skjolber.stcsv;

import java.io.IOException;
import java.io.Reader;

import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.CsvException;

public final class StringArrayCsvReader extends AbstractCsvReader<String[]> {
	
	private final String[] value;
	private final int lastIndex;

	public StringArrayCsvReader(Reader reader, int columns) {
		super(reader, 65536);
		
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
					int rangeIndex = this.getCurrentRange();
					++currentOffset;
					start = currentOffset;
	
					while (true) {
						if (current[currentOffset] <= '"') {
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
									break;
								}
		
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
						++currentOffset;
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
				int rangeIndex = this.getCurrentRange();
				++currentOffset;
				start = currentOffset;

				while (true) {
					if (current[currentOffset] <= '"') {
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
								break;
							}
	
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
					++currentOffset;
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