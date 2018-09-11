package com.github.skjolber.stcsv;

import java.io.IOException;
import java.io.Reader;


/**
 * Base class for CSV data-binding. Generated parsers extend this class. 
 * <br><br>
 * Implementation note: The buffering is done so that reading plain (not quoted)
 * content is speed up at the cost of reading quoted content. Essentially there is a 
 * terminator scheme, where newline is the terminator. 
 * <br><br>
 * Each time the buffer is filled, the underlying implementation scans backwards for a newline.   
 * The quoted parse loop must check whether the buffer must be filled upon encountering a quote. 
 * <br><br>
 * If the file ends without a newline, one is inserted.
 * 
 * @param <T> the target class (output from each line of CSV file). 
 */

public abstract class AbstractCsvReader<T> implements CsvReader<T> {

	public static final int DEFAULT_RANGE_LENGTH = 64 * 1024;
	
	protected final Reader reader;
	protected final char[] current;
	protected final int maxRange;
	
	protected int currentOffset = 0;
	protected int currentRange = 0;
	protected int spareRange = 0;
	protected boolean eof = false;

	public AbstractCsvReader(Reader reader, char[] current, int offset, int length) {
		this.reader = reader;
		this.current = current;
		this.currentOffset = offset;
		this.spareRange = length;
		this.currentRange = findEndOfLine(length - 1);

		// always leave one char for artificially adding a linebreak if necessary
		this.maxRange = current.length - 1;
	}

	protected int findEndOfLine(int currentRange) {
		if(currentRange > 0) {
			// find first end of line
			while(current[currentRange] != '\n') {
				currentRange--;
			}
		}
		return currentRange;
	}
	
	public AbstractCsvReader(Reader reader, int length) {
		this(reader, new char[length + 1], 0, 0);
	}
	
	protected int fill(int keep) throws IOException { 
		this.spareRange -= keep;
		
		return fill();
	}

	protected int fill() throws IOException {
		char[] current = this.current;
		
		int currentRange = this.currentRange;
		
		if(spareRange > currentRange) {
			System.arraycopy(current, currentRange + 1, current, 0, currentRange = spareRange - currentRange - 1);
		} else {
			currentRange = 0;
		}
		
		int read;
		do {
			read = reader.read(current, currentRange, maxRange - currentRange);
			if(read == -1) {
				if(!eof) {
					eof = true;
	
					// artificially insert linebreak so that scanners detects end
					if(currentRange > 0 && current[currentRange - 1] != '\n') {
						current[currentRange] = '\n';
					}
				}
				this.spareRange = currentRange;
				this.currentRange = currentRange;
				
				return currentRange;
			}
			currentRange += read;
		} while(currentRange < maxRange);
		
		this.spareRange = currentRange;
		
		return this.currentRange = findEndOfLine(currentRange);
	}

	public abstract T next() throws Exception;

	public int getCurrentRange() {
		return currentRange;
	}

}
