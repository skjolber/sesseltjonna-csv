package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.CsvMapper;

public class CsvLineObjectScannerDividerTest {

	private CsvMapper<CsvLineObject> mapping;
	
	@BeforeEach
	public void init() throws Exception {
		mapping = CsvMapper.builder(CsvLineObject.class)
				.divider(';')
				.stringField("stringValue")
					.optional()
				.longField("longValue")
					.optional()
				.integerField("integerValue")
					.optional()
				.booleanField("booleanValue")
					.optional()
				.doubleField("doubleValue")
					.optional()
				.build();		
	}

	@Test
	public void testSemiColonAsDivider() throws Exception {
		StringBuffer builder = new StringBuffer("stringValue;longValue;integerValue;d;e;booleanValue;g;doubleValue;i\n");

		String stringValue = "string";
		Long longValue = 1L;
		Integer integerValue = 2;
		Short shortValue = 3;
		Byte byteValue = 4;
		Boolean booleanValue = true;
		Character characterValue = 'a';
		Double doubleValue = 2.5;
		Float floatValue = 7.5f;
		
		builder.append(stringValue);
		builder.append(";");
		builder.append(longValue.toString());
		builder.append(";");
		builder.append(integerValue.toString());
		builder.append(";");
		builder.append(shortValue.toString());
		builder.append(";");
		builder.append(byteValue.toString());
		builder.append(";");
		builder.append(booleanValue.toString());
		builder.append(";");
		builder.append(characterValue.toString());
		builder.append(";");
		builder.append(doubleValue.toString());
		builder.append(";");
		builder.append(floatValue.toString());
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		assertThat(next.getLongValue()).isEqualTo(longValue);
		assertThat(next.getIntegerValue()).isEqualTo(integerValue);
		assertThat(next.getBooleanValue()).isEqualTo(booleanValue);
		assertThat(next.getDoubleValue()).isEqualTo(doubleValue);
		
		assertThat(scanner.next()).isNull();

	}

}
