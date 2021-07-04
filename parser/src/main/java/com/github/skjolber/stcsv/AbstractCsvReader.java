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
	
	@Override
	public void close() throws IOException {
		reader.close();
	}
	
	@Override
	public RawReader getReader() {
		return new RawReader() {
			
			@Override
			public char[] getBuffer() {
				return current;
			}
			
			@Override
			public int getLimit() {
				return dataLength;
			}
			
			@Override
			public int getOffset() {
				return offset;
			}
			
			@Override
			public boolean fill() throws IOException {
				if (AbstractCsvReader.this.fill() <= 0) {
					return false;
				}
				
				return true;
			}
			
		    /**
		     * Reads characters into a portion of an array.
		     * @param   b  Destination buffer
		     * @param   off  Offset at which to start storing characters
		     * @param   len   Maximum number of characters to read
		     * @return  The actual number of characters read, or -1 if
		     *          the end of the stream has been reached
		     *
		     * @throws  IOException  If an I/O error occurs
		     * @throws  IndexOutOfBoundsException {@inheritDoc}
		     */
			
		    public int read(char b[], int off, int len) throws IOException {
				int currentOffset = AbstractCsvReader.this.offset;
				if (currentOffset >= dataLength) {
					currentOffset = dataLength - endOfLineIndex - 1;
					if (AbstractCsvReader.this.fill() <= 0) {
						return -1;
					}
				}

				int avail = dataLength - currentOffset;
				
				if(len > avail) {
					len = avail;
				}
				
	            System.arraycopy(current, currentOffset, b, off, len);
	            
	            AbstractCsvReader.this.offset = currentOffset + len;
	            
	            return len;
		    }
		    
		    /**
		     * Reads a single character.  This method will block until a character is
		     * available, an I/O error occurs, or the end of the stream is reached.
		     *
		     * <p> Subclasses that intend to support efficient single-character input
		     * should override this method.
		     *
		     * @return     The character read, as an integer in the range 0 to 65535
		     *             ({@code 0x00-0xffff}), or -1 if the end of the stream has
		     *             been reached
		     *
		     * @throws     IOException  If an I/O error occurs
		     */
		    
			@Override
			public int read() throws IOException {
				int currentOffset = AbstractCsvReader.this.offset;
				if (currentOffset >= dataLength) {
					currentOffset = dataLength - endOfLineIndex - 1;
					if (AbstractCsvReader.this.fill() <= 0) {
						return -1;
					}
				}
				
				AbstractCsvReader.this.offset = currentOffset + 1;
				
				return current[currentOffset];
			}
			

			public long skip(long count) throws IOException {
				int currentOffset = AbstractCsvReader.this.offset;
				if (currentOffset >= dataLength) {
					currentOffset = dataLength - endOfLineIndex - 1;
					if (AbstractCsvReader.this.fill() <= 0) {
						return -1;
					}
				}

				int len = (int)Math.min(Integer.MAX_VALUE, count);

				int avail = dataLength - currentOffset;
				if(len > avail) {
					len = avail;
				}
	            
	            AbstractCsvReader.this.offset = currentOffset + len;
	            
	            return len;
			}
			
			public boolean skipToCharacter(char c) throws IOException {
				int currentOffset = AbstractCsvReader.this.offset;

				final char[] current = AbstractCsvReader.this.current;

				try {
					do {
						if (currentOffset >= dataLength) {
							currentOffset = dataLength - endOfLineIndex - 1;
							if (AbstractCsvReader.this.fill() <= 0) {
								return false;
							}
						}
					} while (current[currentOffset++] != c);

					AbstractCsvReader.this.offset = currentOffset;
				} catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
				return true;
			}			
			
		    /**
		     * Tells whether this stream supports the mark() operation, which it does not.
		     */
		    public boolean markSupported() {
		        return false;
		    }
			
			@Override
			public void close() throws IOException {
				AbstractCsvReader.this.reader.close();
			}
		};
	}
}
