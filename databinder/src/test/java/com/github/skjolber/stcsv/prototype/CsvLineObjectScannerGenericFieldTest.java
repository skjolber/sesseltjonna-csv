package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.CsvReader;

public class CsvLineObjectScannerGenericFieldTest {

	private CsvMapper<CsvLineObject> mapping1;
	private Map<String, String> internatingMap = new HashMap<>();
	
	@BeforeEach
	public void init() throws Exception {
		mapping1 = CsvMapper.builder(CsvLineObject.class)
				.field("stringValue")
					.consumer((a, b, c, d) -> {
						String v = new String(b, c, d - c);
						String string = internatingMap.get(v);
						if(string == null) {
							internatingMap.put(v, v);
							string = v;
						}
						a.setStringValue(string);
					})
					.required()
				.build();
	}
	
	@Test
	public void testStringInterning() throws Exception {
		String header = "stringValue\n";
		String row = "12345\n";

		Reader stringReader = new StringReader(header + row);

		CsvReader<CsvLineObject> scanner = mapping1.create(stringReader);
		CsvLineObject next = scanner.next();
		assertThat(next.getStringValue()).isEqualTo("12345");
	}
	
}
