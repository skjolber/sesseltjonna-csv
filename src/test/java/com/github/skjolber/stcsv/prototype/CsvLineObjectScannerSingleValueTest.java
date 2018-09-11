package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.CsvMapper;

public class CsvLineObjectScannerSingleValueTest {

	private CsvMapper<CsvLineObject> mapping;

	@BeforeEach
	public void init() throws Exception {
		mapping = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.quoted()
					.required()
				.build();
	}
	
	@Test
	public void testSingleValue() throws Exception {
		String stringValue = "string";
		
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("stringValue");
		builder.append("\n");
		
		// first line
		builder.append(stringValue);
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}
	
	
	@Test
	public void testNewLineCarriageReturn() throws Exception {
		String stringValue = "string";
		
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("stringValue");
		builder.append("\r\n");
		
		// first line
		builder.append(stringValue);
		builder.append("\r\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}	
}
