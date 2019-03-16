package com.github.skjolber.stcsv.builder;

import java.io.Reader;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.EmptyCsvReader;
import com.github.skjolber.stcsv.parser.StringArrayCsvReader;

public class StringArrayCsvReaderBuilder extends AbstractCsvBuilder<StringArrayCsvReaderBuilder> {

	public CsvReader<String[]> build(Reader reader) throws Exception {
		char[] current = new char[bufferLength + 1];

		int start = 0;
		int end = 0;
		do {
			int read = reader.read(current, start, bufferLength - start);
			if(read == -1) {
				return new EmptyCsvReader<>();
			} else {
				end += read;
			}

			for(int i = start; i < end; i++) {
				if(current[i] == '\n') {
					int columns = parseFirstLine(current, end);
					
					// 		boolean carriageReturns = header.length() > 1 && header.charAt(header.length() - 1) == '\r';
					return new StringArrayCsvReader(reader, current, 0, end, columns);
				}
			}
			start += end;
		} while(end < bufferLength);

		throw new IllegalArgumentException("No linebreak found in " + current.length + " characters");
	}
	
	private int parseFirstLine(char[] current, int end) {
		int count = 0;

		for(int i = 0; i < end; i++) {
			count++;
			if(current[i] == quoteCharacter) {
				while(true) {
					++i;
					if(current[i] == escapeCharacter) {
						if(quoteCharacter == escapeCharacter) {
							if (current[i + 1] != quoteCharacter) {
								break;
							}
						} else {
							// skip
							i++;
						}
					}
				}
			} 
			while (current[i] != divider && current[i] != '\n') {
				i++;
			}
		}
		return count;
	}
	
}
