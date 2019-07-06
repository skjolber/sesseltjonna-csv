package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.CsvMapper2;
import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.builder.CsvMappingBuilder2;

public class CsvLineObjectScannerGenericFieldTest {

	private CsvMapper<CsvLineObject> mapping1;
	private CsvMapper2<CsvLineObject, CsvLineObjectScannerGenericFieldTest> mapping2;
	private CsvMapper2<CsvLineObject, CsvLineObjectScannerGenericFieldTest> mapping3;
	
	private Map<String, String> internatingMap = new HashMap<>();
	
	public Map<String, String> getInternatingMap() {
		return internatingMap;
	}
	
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
		
		mapping2 = CsvMapper2.builder(CsvLineObject.class, CsvLineObjectScannerGenericFieldTest.class)
				.field("stringValue")
					.consumer( (a, i, b, c, d) -> {
						
						Map<String, String> map = i.getInternatingMap();
						String v = new String(b, c, d - c);
						String string = map.get(v);
						if(string == null) {
							map.put(v, v);
							string = v;
						}
						a.setStringValue(string);
					})
					.required()
				.build();
		
		mapping3 = CsvMapper2.builder(CsvLineObject.class, CsvLineObjectScannerGenericFieldTest.class)
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
	public void testStringInterningBi() throws Exception {
		String header = "stringValue\n";
		String row = "12345\n";

		Reader stringReader = new StringReader(header + row);

		CsvReader<CsvLineObject> scanner = mapping1.create(stringReader);
		CsvLineObject next = scanner.next();
		assertThat(next.getStringValue()).isEqualTo("12345");
	}

	@Test
	public void testStringInterningTri() throws Exception {
		String header = "stringValue\n";
		String row = "12345\n";

		Reader stringReader = new StringReader(header + row);

		CsvReader<CsvLineObject> scanner = mapping2.create(stringReader, this);
		CsvLineObject next = scanner.next();
		assertThat(next.getStringValue()).isEqualTo("12345");
	}
	
	@Test
	public void testStringInterningTriBi() throws Exception {
		String header = "stringValue\n";
		String row = "12345\n";

		Reader stringReader = new StringReader(header + row);

		CsvReader<CsvLineObject> scanner = mapping3.create(stringReader, this);
		CsvLineObject next = scanner.next();
		assertThat(next.getStringValue()).isEqualTo("12345");
	}	

}
