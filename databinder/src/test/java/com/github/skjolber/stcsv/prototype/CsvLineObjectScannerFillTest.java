package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.CsvReader;

public class CsvLineObjectScannerFillTest {

	private CsvMapper<CsvLineObject> mapping1;
	private CsvMapper<CsvLineObject> mapping2;
	
	@BeforeEach
	public void init() throws Exception {
		mapping1 = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.integerField("integerValue")
					.required()
				.longField("longValue")
					.required()
				.stringField("stringValue")
					.quoted()
					.required()
				.build();
		
		mapping2 = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.escapeCharacter('\\')
				.integerField("integerValue")
					.required()
				.longField("longValue")
					.required()
				.stringField("stringValue")
					.quoted()
					.required()
				.build();
	}
	
	@Test
	public void parseQuotedNewlineWithFillBufferFirstOrMiddleColumn() throws Exception {
		String header = "integerValue,stringValue,longValue";
		String row = "12345,\"b1\nb2b3b4\",67890\n";

		int index = row.indexOf('\n');

		char[] first = row.substring(0, index+1).toCharArray();
		
		char[] b = new char[1024];
		
		System.arraycopy(first, 0, b, 0, first.length);
		StringReader stringReader = new StringReader(row.substring(index + 1));

		CsvReader<CsvLineObject> scanner = mapping1.create(stringReader, header, b, 0, first.length);
		CsvLineObject next = scanner.next();
		assertThat(next.getIntegerValue()).isEqualTo(12345);
		assertThat(next.getStringValue()).isEqualTo("b1\nb2b3b4");
		assertThat(next.getLongValue()).isEqualTo(67890);

		System.arraycopy(first, 0, b, 0, first.length);
		stringReader = new StringReader(row.substring(index + 1));

		scanner = mapping2.create(stringReader, header, b, 0, first.length);
		next = scanner.next();
		assertThat(next.getIntegerValue()).isEqualTo(12345);
		assertThat(next.getStringValue()).isEqualTo("b1\nb2b3b4");
		assertThat(next.getLongValue()).isEqualTo(67890);
	}
	
	@Test
	public void parseQuotedNewlineWithFillBufferEndColumn() throws Exception {
		String header = "integerValue,longValue,stringValue";
		String row = "12345,67890,\"b1\nb2b3b4\"\n";

		int index = row.indexOf('\n');

		char[] first = row.substring(0, index+1).toCharArray();
		
		char[] b = new char[1024];
		
		System.arraycopy(first, 0, b, 0, first.length);
		StringReader stringReader = new StringReader(row.substring(index + 1));

		CsvReader<CsvLineObject> scanner = mapping1.create(stringReader, header, b, 0, first.length);
		CsvLineObject next = scanner.next();
		assertThat(next.getIntegerValue()).isEqualTo(12345);
		assertThat(next.getStringValue()).isEqualTo("b1\nb2b3b4");
		assertThat(next.getLongValue()).isEqualTo(67890);

		System.arraycopy(first, 0, b, 0, first.length);
		stringReader = new StringReader(row.substring(index + 1));

		scanner = mapping2.create(stringReader, header, b, 0, first.length);
		next = scanner.next();
		assertThat(next.getIntegerValue()).isEqualTo(12345);
		assertThat(next.getStringValue()).isEqualTo("b1\nb2b3b4");
		assertThat(next.getLongValue()).isEqualTo(67890);
	}
	
}
