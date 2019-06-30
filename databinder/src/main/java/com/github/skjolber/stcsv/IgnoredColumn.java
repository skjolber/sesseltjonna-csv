package com.github.skjolber.stcsv;

import java.io.IOException;

public class IgnoredColumn {

	private IgnoredColumn() {
	}
	
	public static class IdenticalQuoteAndEscapeCharacter {

		private IdenticalQuoteAndEscapeCharacter() {
		}

		
		public static int skipColumns(AbstractCsvReader scanner, char[] current, int currentOffset, int divider, int quoteCharacter, int columns) throws IOException {
			
			while(columns > 0) {
				if(current[currentOffset] == divider) {
					--columns;
				} else if(current[currentOffset] == quoteCharacter) {
					int currentRange = scanner.getCurrentRange();
					do {
						if(current[++currentOffset] == quoteCharacter) {
							if(current[currentOffset + 1] != quoteCharacter) {
								// 1x qoute
								break;
							}
							// 2x qoute
							++currentOffset;
						} else if(current[currentOffset] == '\n') {
							if(currentOffset == currentRange) {
								if((currentRange = scanner.fill(0)) == 0) { // must get more bytes
									throw new CsvException();
								}
								currentOffset = 0;
							} else {
								++currentOffset;
							}
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
		
		
		public static int skipToLineBreak(AbstractCsvReader scanner, char[] current, int currentOffset, int quoteCharacter) throws IOException {
			while(true) {
				if(current[currentOffset] == '\n') {
					return ++currentOffset; // skip newline
				} else if(current[currentOffset] == quoteCharacter) {
					int currentRange = scanner.getCurrentRange();
					do {
						if(current[++currentOffset] == quoteCharacter) {
							if(current[currentOffset + 1] != quoteCharacter) {
								// 1x qoute
								break;
							}
							// 2x qoute
							++currentOffset;
						} else if(current[currentOffset] == '\n') {
							if(currentOffset == currentRange) {
								if((currentRange = scanner.fill(0)) == 0) { // must get more bytes
									throw new CsvException();
								}
								currentOffset = 0;
							} else {
								++currentOffset;
							}
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
		
		public static int skipColumns(AbstractCsvReader scanner, char[] current, int currentOffset, int divider, int quoteCharacter, int escapeCharacter, int columns) throws IOException {
			
			while(columns > 0) {
				if(current[currentOffset] == divider) {
					--columns;
				} else if(current[currentOffset] == quoteCharacter) {
					int currentRange = scanner.getCurrentRange();
					do {
						if(current[++currentOffset] == quoteCharacter) {
							break;
						} else if(current[currentOffset] == escapeCharacter) {
							currentOffset++; // skip next character
						} else if(currentOffset == currentRange) {
							if((currentRange = scanner.fill(0)) == 0) { // must get more bytes
								throw new CsvException();
							}
							currentOffset = 0;
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
		
		
		public static int skipToLineBreak(AbstractCsvReader scanner, char[] current, int currentOffset, int quoteCharacter, int escapeCharacter) throws IOException {
			while(true) {
				if(current[currentOffset] == '\n') {
					return ++currentOffset; // skip newline
				} else if(current[currentOffset] == quoteCharacter) {
					int currentRange = scanner.getCurrentRange();
					do {
						if(current[++currentOffset] == quoteCharacter) {
							break;
						} else if(current[currentOffset] == escapeCharacter) {
							currentOffset++; // skip next character
						} else if(currentOffset == currentRange) {
							if((currentRange = scanner.fill(0)) == 0) { // must get more bytes
								throw new CsvException();
							}
							currentOffset = 0;
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
