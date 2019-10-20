package com.github.skjolber.stcsv.databinder.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.databinder.CsvMapper;

public class CsvLineObjectScannerSkipCommentTest {

	@Test
	public void testSkipSingleComment() throws Exception {

		CsvMapper<CsvLineObject> mapping = CsvMapper.builder(CsvLineObject.class)
				.skipComments()
				.stringField("stringValue")
					.consumer(CsvLineObject::setStringValue)
					.required()
				.build();

		// newline
		String stringValueForLinebreak = createCsv("\n", "# this is a comment", 1);

		CsvReader<CsvLineObject> scannerForLinebreak = mapping.create(new StringReader(stringValueForLinebreak));
		CsvLineObject next = scannerForLinebreak.next();
		assertThat(next).isNotNull();
		assertThat(next.getStringValue()).isEqualTo("string");
		assertThat(scannerForLinebreak.next()).isNull();
		
		// newline carriage return
		String stringValue = createCsv("\r\n", "# this is a comment", 1);

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(stringValue));
		next = scanner.next();
		assertThat(next).isNotNull();
		assertThat(next.getStringValue()).isEqualTo("string");
		assertThat(scanner.next()).isNull();
		
	}
	

	@Test
	public void testSkipMultipleComments() throws Exception {

		CsvMapper<CsvLineObject> mapping = CsvMapper.builder(CsvLineObject.class)
				.skipComments()
				.stringField("stringValue")
					.consumer(CsvLineObject::setStringValue)
					.required()
				.build();

		// newline
		String stringValueForLinebreak = createCsv("\n", "# this is a comment", 3);

		CsvReader<CsvLineObject> scannerForLinebreak = mapping.create(new StringReader(stringValueForLinebreak));
		CsvLineObject next = scannerForLinebreak.next();
		assertThat(next).isNotNull();
		assertThat(next.getStringValue()).isEqualTo("string");
		assertThat(scannerForLinebreak.next()).isNull();
		
		// newline carriage return
		String stringValue = createCsv("\r\n", "# this is a comment", 3);

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(stringValue));
		next = scanner.next();
		assertThat(next).isNotNull();
		assertThat(next.getStringValue()).isEqualTo("string");
		assertThat(scanner.next()).isNull();
		
	}
		
	
	@Test
	public void testSkipMultipleCommentsAndLineBreak() throws Exception {

		CsvMapper<CsvLineObject> mapping = CsvMapper.builder(CsvLineObject.class)
				.skipComments()
				.skipEmptyLines()
				.stringField("stringValue")
					.consumer(CsvLineObject::setStringValue)
					.required()
				.build();

		// newline
		String stringValueForLinebreak = createCsv("\n", "\n# this is a comment\n", 3);

		CsvReader<CsvLineObject> scannerForLinebreak = mapping.create(new StringReader(stringValueForLinebreak));
		CsvLineObject next = scannerForLinebreak.next();
		assertThat(next).isNotNull();
		assertThat(next.getStringValue()).isEqualTo("string");
		assertThat(scannerForLinebreak.next()).isNull();
		
		// newline carriage return
		String stringValue = createCsv("\r\n", "\r\n# this is a comment\r\n", 3);

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(stringValue));
		next = scanner.next();
		assertThat(next).isNotNull();
		assertThat(next.getStringValue()).isEqualTo("string");
		assertThat(scanner.next()).isNull();
		
	}	

	private String createCsv(String linebreak, String comment, int count) {
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
		
		// comment - to be skipped
		for(int i = 0; i < count; i++) {
			builder.append(comment);
			builder.append(linebreak);
		}
		
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
