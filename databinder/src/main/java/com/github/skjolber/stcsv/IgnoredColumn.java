package com.github.skjolber.stcsv;

import java.io.IOException;

public class IgnoredColumn {

	@SuppressWarnings("unchecked")
	public static int skipColumns(AbstractCsvReader scanner, char[] current, int currentOffset, char c, int columns) throws IOException {
		
		while(columns > 0) {
			if(current[currentOffset] == c) {
				--columns;
			} else if(current[currentOffset] == '"') {
				int currentRange = scanner.getCurrentRange();
				do {
					if(current[++currentOffset] == '"') {
						if(current[currentOffset + 1] != '"') {
							// 1x qoute
							break;
						}
						// 2x qoute
						++currentOffset;
					} else if(current[currentOffset] == '\n') {
						if(currentOffset == currentRange) {
							if((currentRange = scanner.fill(currentOffset)) == 0) { // must get more bytes
								throw new IllegalArgumentException();
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
	
	@SuppressWarnings("unchecked")
	public static int skipColumnsWithoutLinebreak(char[] current, int currentOffset, char c, int columns) throws IOException {
		
		while(columns > 0) {
			if(current[currentOffset] == c) {
				--columns;
			} else if(current[currentOffset] == '"') {
				do {
					if(current[++currentOffset] == '"') {
						if(current[currentOffset + 1] != '"') {
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
	
	@SuppressWarnings("unchecked")
	public static int skipToLineBreak(AbstractCsvReader scanner, char[] current, int currentOffset) throws IOException {
		while(true) {
			if(current[currentOffset] == '\n') {
				return ++currentOffset; // skip newline
			} else if(current[currentOffset] == '"') {
				int currentRange = scanner.getCurrentRange();
				do {
					if(current[++currentOffset] == '"') {
						if(current[currentOffset + 1] != '"') {
							// 1x qoute
							break;
						}
						// 2x qoute
						++currentOffset;
					} else if(current[currentOffset] == '\n') {
						if(currentOffset == currentRange) {
							if((currentRange = scanner.fill(currentOffset)) == 0) { // must get more bytes
								throw new IllegalArgumentException();
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
	
	@SuppressWarnings("unchecked")
	public static int skipToLineBreakWithoutLinebreak(char[] current, int currentOffset) throws IOException {
		while(true) {
			if(current[currentOffset] == '\n') {
				return ++currentOffset; // skip newline
			} else if(current[currentOffset] == '"') {
				do {
					if(current[++currentOffset] == '"') {
						if(current[currentOffset + 1] != '"') {
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
