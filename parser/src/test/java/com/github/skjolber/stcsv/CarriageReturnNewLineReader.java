package com.github.skjolber.stcsv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class CarriageReturnNewLineReader extends Reader {

	private BufferedReader reader;
	private StringBuilder buffer = new StringBuilder();

	public CarriageReturnNewLineReader(Reader reader) {
		super();
		this.reader = new BufferedReader(reader);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public int read(char[] b, int offset, int length) throws IOException {

		while(buffer.length() < length) {
			String readLine = reader.readLine();
			if(readLine != null) {
				buffer.append(readLine);
				buffer.append('\r');
				buffer.append('\n');
			} else {
				break;
			}
		}

		if(buffer.length() == 0) {
			return -1;
		}

		int read = Math.min(length, buffer.length());
		buffer.getChars(0, read, b, offset);
		
		buffer.delete(0, read);
		
		return read;
	}
	
}
