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
	protected final int maxDataLength;
	
	protected int offset = 0;
	protected int endOfLineIndex = 0; // last newline within the buffer
	protected int dataLength = 0; // data length
	
	protected boolean eof = false;

	/**
	 * Construct new instance. The current buffer must at least contain a single row, 
	 * ending with a newline. 
	 * 
	 * @param reader read input
	 * @param current current buffer
	 * @param offset buffer offset
	 * @param length buffer payload length
	 */
	
	public AbstractCsvReader(Reader reader, char[] current, int offset, int length) {
		this.reader = reader;
		this.current = current;
		this.offset = offset;
		this.dataLength = length;
		this.endOfLineIndex = findEndOfLine(length - 1);
		// always leave one char for artificially adding a linebreak if necessary
		this.maxDataLength = current.length - 1;
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
	
	public int fill(int keep) throws IOException { 
		this.endOfLineIndex -= keep;
		return fill();
	}

	public int fill() throws IOException {
		char[] current = this.current;
		
		// 012345 6789012
		// a,b,c\nd,e,f\n
		int dataLength = this.dataLength - endOfLineIndex - 1;
		if(dataLength > 0) {
			// copy tail to head
			System.arraycopy(current, endOfLineIndex + 1, current, 0, dataLength);
		}
		
		if(eof) {
			this.dataLength = dataLength;
			this.endOfLineIndex = dataLength - 1;
			
			return endOfLineIndex;
		}
		
		int read;
		while(dataLength < maxDataLength) {
			read = reader.read(current, dataLength, maxDataLength - dataLength);
			if(read == -1) {
				eof = true;

				if(dataLength > 0 && current[dataLength - 1] != '\n') {
					// artificially insert linebreak after last line 
					// so that scanners detects end
					current[dataLength] = '\n';
					dataLength++;
				}
				this.endOfLineIndex = dataLength - 1;
				this.dataLength = dataLength;
				
				return this.endOfLineIndex;
			}
			dataLength += read;
		}
		
		this.dataLength = dataLength;
		
		return this.endOfLineIndex = findEndOfLine(dataLength - 1);
	}

	public abstract T next() throws Exception;

	public int getEndOfLineIndex() {
		return endOfLineIndex;
	}

}
