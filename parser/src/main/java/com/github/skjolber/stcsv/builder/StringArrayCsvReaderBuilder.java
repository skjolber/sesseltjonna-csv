package com.github.skjolber.stcsv.builder;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.EmptyCsvReader;
import com.github.skjolber.stcsv.sa.DefaultStringArrayCsvReader;
import com.github.skjolber.stcsv.sa.NoLinebreakStringArrayCsvReader;
import com.github.skjolber.stcsv.sa.rfc4180.NoLinebreakRFC4180StringArrayCsvReader;
import com.github.skjolber.stcsv.sa.rfc4180.RFC4180StringArrayCsvReader;

public class StringArrayCsvReaderBuilder extends AbstractCsvBuilder<StringArrayCsvReaderBuilder> {

	protected boolean linebreaks = true;
	protected Map<String, Integer> columnIndexes;

	public CsvReader<String[]> build(Reader reader) throws Exception {
		CsvReader<String[]> r = reader(reader);
		if(columnIndexes != null) {
			String[] next = r.next();
			if(next != null) {
				List<Integer> source = new ArrayList<>();
				List<Integer> destination = new ArrayList<>();
				
				int maxDestination = -1;
				for(int i = 0; i < next.length; i++) {
					String n = next[i];
					
					Integer integer = columnIndexes.get(n);
					if(integer != null) {
						source.add(i);
						destination.add(integer);
						
						if(maxDestination < integer) {
							maxDestination = integer;
						}
					}
				}

				int[] s = new int[source.size()];
				int[] d = new int[source.size()];
				for(int i = 0; i < s.length; i++) {
					s[i] = source.get(i);
					d[i] = destination.get(i);
				}
				
				return new FixedIndexCsvReader(r, s, d, maxDestination + 1);
				
			}
		}
		return r;
	}

	private CsvReader<String[]> reader(Reader reader) throws IOException {
		char[] current = new char[bufferLength + 1];

		int offset = 0;
		do {
			int read = reader.read(current, offset, bufferLength - offset);
			if(read == -1) {
				break;
			} else {
				offset += read;
			}
		} while(offset < bufferLength);
		
		if(offset == 0) {
			return new EmptyCsvReader<>();			
		}
		
		int columns = countColumnsLine(current, offset);

		
		if(divider == ',' && quoteCharacter == '"' && escapeCharacter == '"') {
			if(!linebreaks) {
				return new NoLinebreakRFC4180StringArrayCsvReader(reader, current, 0, offset, columns);
			}
			return new RFC4180StringArrayCsvReader(reader, current, 0, offset, columns);
		}
		
		if(!linebreaks) {
			return new NoLinebreakStringArrayCsvReader(reader, current, 0, offset, columns, quoteCharacter, escapeCharacter, divider);
		}
		
		return new DefaultStringArrayCsvReader(reader, current, 0, offset, columns, quoteCharacter, escapeCharacter, divider);
	}
	
	private int countColumnsLine(char[] current, int end) {
		int count = 0;

		for(int i = 0; i < end; i++) {
			count++;
			if(current[i] == quoteCharacter) {
				while(true) {
					++i;
					if(current[i] == escapeCharacter) {
						if(quoteCharacter == escapeCharacter) {
							i++;
							if (current[i] != quoteCharacter) {
								break;
							}
						} else {
							// skip single character
							i++;
						}
					}
				}
			}
			while(true) {
				if(current[i] == divider) {
					break;
				} else if(current[i] == '\n') {
					return count;
				}
				i++;
			}
		}
		return count;
	}
	
	public StringArrayCsvReaderBuilder quotedWithoutLinebreaks() {
		this.linebreaks = false;
		return this;
	}

	public StringArrayCsvReaderBuilder withColumnMappings(Map<String, Integer> map) {
		this.columnIndexes = map;
		return this;
	}

	public StringArrayCsvReaderBuilder withColumnMapping(String sourceColumnName, int destinationIndex) {
		if(this.columnIndexes == null) {
			this.columnIndexes = new HashMap<>(32);
		}
		this.columnIndexes.put(sourceColumnName, destinationIndex);
		
		return this;
	}

}
