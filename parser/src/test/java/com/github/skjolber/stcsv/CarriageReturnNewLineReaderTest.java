package com.github.skjolber.stcsv;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class CarriageReturnNewLineReaderTest {

	@Test
	public void test() throws IOException {
		StringReader reader = new StringReader("a\nb\n");
		
		CarriageReturnNewLineReader r = new CarriageReturnNewLineReader(reader);
		
		char[] buffer = new char[1024];
		int read = r.read(buffer, 0, buffer.length);
		
		assertThat(new String(buffer, 0, read)).isEqualTo("a\r\nb\r\n");
		
		r.close();
	}
}
