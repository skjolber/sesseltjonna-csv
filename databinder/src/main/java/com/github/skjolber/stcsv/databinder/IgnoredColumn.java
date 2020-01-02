package com.github.skjolber.stcsv.databinder;

import java.io.IOException;

import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.CsvException;

// TODO introduce max characters for simple inner loop

public class IgnoredColumn {

	private IgnoredColumn() {
	}
	
	public static class IdenticalQuoteAndEscapeCharacter {

		private IdenticalQuoteAndEscapeCharacter() {
		}

		
		public static int skipColumns(AbstractCsvReader<?> scanner, char[] current, int currentOffset, int divider, int quoteCharacter, int columns) throws IOException {
			
			columns: 
			while(columns > 0) {
				if(current[currentOffset] == divider) {
					--columns;
				} else if(current[currentOffset] == quoteCharacter) {
					int currentRange = scanner.getEndOfLineIndex();
					currentOffset++;
					do {
						if(current[currentOffset] == quoteCharacter) {
							++currentOffset;
							
							if(currentOffset == currentRange) {
								if((currentRange = scanner.fill(0)) == 0) { 
									// 1s quote, but expected more bytes since missing at least one divider
									throw new CsvException("Unexpected end of input");
								}
								currentOffset = 0;
							}
							
							if(current[currentOffset] != quoteCharacter) {
								// 1x qoute
								continue columns;
							}
							// 2x qoute
							++currentOffset;
						} else if(currentOffset == currentRange) {
							if((currentRange = scanner.fill(0)) == 0) { 
								// expected more bytes since still within quote
								throw new CsvException("Unexpected end of input");
							}
							currentOffset = 0;
						} else {
							++currentOffset;
						}
					} while(true);
				}
				currentOffset++;
			}
			
			return currentOffset;
		}
		
		
		public static int skipColumnsWithoutLinebreak(char[] current, int currentOffset, int divider, int quoteCharacter, int columns) throws IOException {
			
			while(columns > 0) {
				if(current[currentOffset] == divider) {
					--columns;
				} else if(current[currentOffset] == quoteCharacter) {
					do {
						if(current[++currentOffset] == quoteCharacter) {
							if(current[currentOffset + 1] != quoteCharacter) {
								// 1x qoute
								break;
							}
							// 2x qoute
							++currentOffset;
						}
					} while(true);
				}
				currentOffset++;
			}
			
			return currentOffset;
		}	
		
		
		public static int skipToLineBreak(AbstractCsvReader<?> scanner, char[] current, int currentOffset, int quoteCharacter) throws IOException {
			columns:
			while(true) {
				if(current[currentOffset] == '\n') {
					return ++currentOffset; // skip newline
				} else if(current[currentOffset] == quoteCharacter) {
					int currentRange = scanner.getEndOfLineIndex();
					currentOffset++;
					do {
						if(current[currentOffset] == quoteCharacter) {
							++currentOffset;
							
							if(currentOffset == currentRange) {
								if((currentRange = scanner.fill(0)) == 0) { 
									// 1x quote, end of file
									
									return ++currentOffset; // skip newline
								}
								currentOffset = 0;
							}
							
							if(current[currentOffset] != quoteCharacter) {
								// 1x qoute
								continue columns;
							}
							// 2x qoute
							++currentOffset;
						} else if(currentOffset == currentRange) {
							if((currentRange = scanner.fill(0)) == 0) { // must get more bytes since still within quote
								throw new CsvException();
							}
							currentOffset = 0;
						} else {
							++currentOffset;
						}
					} while(true);
				}
				currentOffset++;
			}
		}
		
		
		public static int skipToLineBreakWithoutLinebreak(char[] current, int currentOffset, int quoteCharacter) throws IOException {
			while(true) {
				if(current[currentOffset] == '\n') {
					return ++currentOffset; // skip newline
				} else if(current[currentOffset] == quoteCharacter) {
					do {
						if(current[++currentOffset] == quoteCharacter) {
							if(current[currentOffset + 1] != quoteCharacter) {
								// 1x qoute
								break;
							}
							// 2x qoute
							++currentOffset;
						}
					} while(true);
				}
				currentOffset++;
			}
		}
	}

	public static class DifferentQuoteAndEscapeCharacter {

		private DifferentQuoteAndEscapeCharacter() {
		}
		
		public static int skipColumns(AbstractCsvReader<?> scanner, char[] current, int currentOffset, int divider, int quoteCharacter, int escapeCharacter, int columns) throws IOException {
			
			while(columns > 0) {
				if(current[currentOffset] == divider) {
					--columns;
				} else if(current[currentOffset] == quoteCharacter) {
					int currentRange = scanner.getEndOfLineIndex();
					currentOffset++;
					
					do {
						if(current[currentOffset] == quoteCharacter) {
							break;
						} else if(current[currentOffset] == escapeCharacter) { // so is at least one more character, since last is always newline
							currentOffset++; // skip next character

							if(currentOffset == currentRange) {
								if((currentRange = scanner.fill(0)) == 0) {
									// exptected more bytes since missing at least one divider
									throw new CsvException("Unexpected end of input");
								}
								// skip now
								currentOffset = 1; 
							}
						}
						if(currentOffset == currentRange) {
							if((currentRange = scanner.fill(0)) == 0) {
								// exptected more bytes since missing at least one divider
								throw new CsvException("Unexpected end of input");
							}
							currentOffset = 0;
						} else {
							currentOffset++;
						}
					} while(true);
					
				}
				currentOffset++;
			}
			
			return currentOffset;
		}
		
		
		public static int skipColumnsWithoutLinebreak(char[] current, int currentOffset, int divider, int quoteCharacter, int escapeCharacter, int columns) throws IOException {
			
			while(columns > 0) {
				if(current[currentOffset] == divider) {
					--columns;
				} else if(current[currentOffset] == quoteCharacter) {
					do {
						if(current[++currentOffset] == quoteCharacter) {
							break;
						} else if(current[currentOffset] == escapeCharacter) {
							currentOffset++; // skip next character
						}
					} while(true);
				}
				currentOffset++;
			}
			
			return currentOffset;
		}	
		
		
		public static int skipToLineBreak(AbstractCsvReader<?> scanner, char[] current, int currentOffset, int quoteCharacter, int escapeCharacter) throws IOException {
			while(true) {
				if(current[currentOffset] == '\n') {
					return ++currentOffset; // skip newline
				} else if(current[currentOffset] == quoteCharacter) {
					int currentRange = scanner.getEndOfLineIndex();
					currentOffset++;
					do {
						if(current[currentOffset] == quoteCharacter) {
							break;
						} else if(current[currentOffset] == escapeCharacter) { // so is at least one more character, since last is always newline
							currentOffset++; // skip next character
						}
						if(currentOffset == currentRange) { // i.e. current character is newline
							if((currentRange = scanner.fill(0)) == 0) { 
								// expected more bytes, still within quote
								throw new CsvException("Unexpected end of input");
							}
							currentOffset = 0;
						} else {
							currentOffset++;
						}
					} while(true);
				}
				currentOffset++;
			}
		}
		
		
		public static int skipToLineBreakWithoutLinebreak(char[] current, int currentOffset, int quoteCharacter, int escapeCharacter) throws IOException {
			while(true) {
				if(current[currentOffset] == '\n') {
					return ++currentOffset; // skip newline
				} else if(current[currentOffset] == quoteCharacter) {
					do {
						if(current[++currentOffset] == quoteCharacter) {
							break;
						} else if(current[currentOffset] == escapeCharacter) {
							currentOffset++; // skip next character
						}
					} while(true);
				}
				currentOffset++;
			}
		}
	}
	
}
