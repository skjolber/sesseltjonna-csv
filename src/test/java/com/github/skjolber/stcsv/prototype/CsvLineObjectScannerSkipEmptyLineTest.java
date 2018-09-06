package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvClassFactory;
import com.github.skjolber.stcsv.CsvClassMapping;

public class CsvLineObjectScannerSkipEmptyLineTest {

	@Test
	public void testSkipEmptyLine() throws Exception {

		CsvClassMapping<CsvLineObject> mapping = CsvClassMapping.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.consumer(CsvLineObject::setStringValue)
					.required()
				.build();

		// newline
		String stringValueForLinebreak = createCsv("\n");

		CsvClassFactory<CsvLineObject> scannerForLinebreak = mapping.create(new StringReader(stringValueForLinebreak));
		CsvLineObject next = scannerForLinebreak.next();
		assertThat(next).isNotNull();
		assertThat(next.getStringValue()).isEqualTo("string");
		assertThat(scannerForLinebreak.next()).isNull();
		
		// newline carriage return
		String stringValue = createCsv("\r\n");

		CsvClassFactory<CsvLineObject> scanner = mapping.create(new StringReader(stringValue));
		next = scanner.next();
		assertThat(next).isNotNull();
		assertThat(next.getStringValue()).isEqualTo("string");
		assertThat(scanner.next()).isNull();
		
	}

	private String createCsv(String linebreak) {
		String stringValue = "string";
		Long longValue = 1L;
		Integer integerValue = 2;
		Short shortValue = 3;
		Byte byteValue = 4;
		Boolean booleanValue = true;
		Character characterValue = 'a';
		Double doubleValue = 2.5;
		Float floatValue = 7.5f;
		
		StringBuffer builder = new StringBuffer();

		// first line
		builder.append("stringValue");
		builder.append(",");
		builder.append("longValue");
		builder.append(",");
		builder.append("integerValue");
		builder.append(",");
		builder.append("shortValue");
		builder.append(",");
		builder.append("byteValue");
		builder.append(",");
		builder.append("booleanValue");
		builder.append(",");
		builder.append("characterValue");
		builder.append(",");
		builder.append("doubleValue");
		builder.append(",");
		builder.append("floatValue");
		builder.append(linebreak);
		
		// empty line - to be skipped
		builder.append(linebreak);
		
		// value line
		builder.append(stringValue);
		builder.append(",");
		builder.append(longValue.toString());
		builder.append(",");
		builder.append(integerValue.toString());
		builder.append(",");
		builder.append(shortValue.toString());
		builder.append(",");
		builder.append(byteValue.toString());
		builder.append(",");
		builder.append(booleanValue.toString());
		builder.append(",");
		builder.append(characterValue.toString());
		builder.append(",");
		builder.append(doubleValue.toString());
		builder.append(",");
		builder.append(floatValue.toString());
		builder.append(linebreak);
		
		return builder.toString();
	}
	
}
