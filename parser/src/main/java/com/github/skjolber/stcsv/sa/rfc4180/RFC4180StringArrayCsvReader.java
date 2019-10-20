package com.github.skjolber.stcsv.sa.rfc4180;

import java.io.IOException;
import java.io.Reader;

import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.sa.StringArrayCsvReader;

public final class RFC4180StringArrayCsvReader extends StringArrayCsvReader {
	
	protected final String[] value;
	protected final int lastIndex;

	public RFC4180StringArrayCsvReader(Reader reader, int columns) {
		super(reader, 65536);
		
		this.value = new String[columns];
		this.lastIndex = columns - 1;
	}

	public RFC4180StringArrayCsvReader(Reader reader, char[] current, int offset, int length, int columns) {
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

		final char[] current = super.current;

		try {
			final String[] value = this.value;

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
					start = currentOffset + 1; // do not include quote character


					quoted : 
					while (true) {
						while (current[++currentOffset] > '"');
						
						if (current[currentOffset] == '"') {
							
							// we're in the middle column, so there should never be a single quote followed by a newline,
							// so checking against the rangeIndex is strictly not necessary

							currentOffset++;
							if (currentOffset == rangeIndex) {
								currentOffset -= start;
								
								// attempt to fill
								currentOffset -= start;
								if ((rangeIndex = this.fill(currentOffset + 1)) <= currentOffset + 1) {
									// expected more bytes; EOF not acceptable unless last column
									throw new CsvException("Illegal value in column " + i);
								}

								start = 0;
							}
							
							if (current[currentOffset] != '"') {
								// single quote
								if (currentOffset - 1 > start) {
									value[i] = new String(current, start, currentOffset - start - 1);
								} else {
									value[i] = null;
								}

								while (current[currentOffset] != ',') {
									++currentOffset;
								}
								
								break quoted;
							}

							// double quote, i.e. convert 2x double quote to 1x double quote
							//
							// equivalent to 
							// System.arraycopy(current, start, current, start + 1, currentOffset - start - 1);
							// ++start;
							System.arraycopy(current, start, current, ++start, currentOffset - start);
						} else if (currentOffset == rangeIndex) {
							currentOffset -= start;
							if ((rangeIndex = this.fill(currentOffset + 1)) <= currentOffset + 1) {
								throw new CsvException("Illegal value in column " + i);
							}

							start = 0;
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
						if(currentOffset - 1 == start) {
							value[lastIndex] = null;
						} else {
							value[lastIndex] = new String(current, start, currentOffset - start - 1);
						}
					} else {
						value[lastIndex] = new String(current, start, currentOffset - start);
					}
				} else {
					value[lastIndex] = null;
				}
			} else {
				int rangeIndex = this.getCurrentRange();
				start = currentOffset + 1; // do not include quote character

				quoted : 
				while (true) {
					while (current[++currentOffset] > '"');
					
					if (current[currentOffset] == '"') {
						
						currentOffset++;
						if (currentOffset == rangeIndex) {
							currentOffset -= start;
							
							// attempt to fill, if we're at EOF thats okey
							rangeIndex = this.fill(currentOffset + 1);

							start = 0;
						}
						
						if (current[currentOffset] != '"') {
							// single quote
							if (currentOffset - 1 > start) {
								value[lastIndex] = new String(current, start, currentOffset - start - 1);
							} else {
								value[lastIndex] = null;
							}

							while (current[currentOffset] != '\n') { // i.e. skip \r 
								++currentOffset;
							}
							
							break quoted;
						}

						// double quote, i.e. convert 2x double quote to 1x double quote
						//
						// equivalent to 
						// System.arraycopy(current, start, current, start + 1, currentOffset - start - 1);
						// ++start;
						System.arraycopy(current, start, current, ++start, currentOffset - start);
						
					} else if (currentOffset == rangeIndex) {
						currentOffset -= start;
						if ((rangeIndex = this.fill(currentOffset + 1)) <= currentOffset + 1) {
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