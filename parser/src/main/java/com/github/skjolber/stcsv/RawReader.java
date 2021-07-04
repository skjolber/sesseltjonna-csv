package com.github.skjolber.stcsv;

import java.io.IOException;
import java.io.Reader;

/**
 * 
 * Access to underlying reader, for error correction and such.
 * 
 */

public abstract class RawReader extends Reader {

	public abstract char[] getBuffer();
	
	/**
	 * 
	 * Index (relative to buffer)
	 * 
	 * @return offset
	 */
	
	public abstract int getOffset();
	
	/**
	 * Limit (so that index is less than limit).
	 * 
	 * @return limit
	 */
	
	public abstract int getLimit();
	
	/**
	 * 
	 * Fill buffer with additional data from the underlying reader.
	 * 
	 * @return true if there was more left
	 * @throws IOException if underlying reader throws exception
	 */
	
	public abstract boolean fill() throws IOException;
}
