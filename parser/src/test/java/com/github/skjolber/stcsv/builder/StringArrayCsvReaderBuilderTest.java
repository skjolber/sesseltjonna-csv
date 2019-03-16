package com.github.skjolber.stcsv.builder;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;

public class StringArrayCsvReaderBuilderTest {

	private String empty = "";
	private String singleLine = "a,b,c\n";
	private String[] emptyColumns = {",,\n", "a,b,\n", "a,,\n"};
	
	@Test
	public void testSingleLine() throws Exception {
		new StringArrayCsvReaderBuilder().build(new StringReader(singleLine));
	}
	
	@Test
	public void testEmptyColumns() throws Exception {
		for(String empty : emptyColumns) {
			new StringArrayCsvReaderBuilder().build(new StringReader(empty));
		}
	}
}
