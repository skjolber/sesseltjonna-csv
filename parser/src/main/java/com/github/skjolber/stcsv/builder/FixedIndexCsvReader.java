package com.github.skjolber.stcsv.builder;

import java.io.Reader;

import com.github.skjolber.stcsv.RawReader;
import com.github.skjolber.stcsv.CsvReader;

/**
 * 
 * Wrappper {@linkplain CsvReader} which normalizes / fixes the line string array 
 * indexes as seen from the consumer's perspective.
 * 
 */

public class FixedIndexCsvReader implements CsvReader<String[]>{

	private final CsvReader<String[]> reader;
	
	private final int[] source;
	private final int[] destination;
	
	private String[] fixed;
	
	public FixedIndexCsvReader(CsvReader<String[]> reader, int[] source, int[] destination, int length) {
		this.reader = reader;
		this.source = source;
		this.destination = destination;
		
		this.fixed = new String[length];
	}

	@Override
	public String[] next() throws Exception {
		String[] next = reader.next();
		if(next == null) {
			return null;
		}
		
		for(int i = 0; i < source.length; i++) {
			fixed[destination[i]] = next[source[i]];
		}
		
		return fixed;
	}

	@Override
	public void close() throws Exception {
		reader.close();
	}
	
	@Override
	public RawReader getReader() {
		return reader.getReader();
	}
}
