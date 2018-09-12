package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.CsvMapper;

public class CsvLineObjectScannerEscapeTest {

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
	public void testEscapeWithNoSpecialCharacter() throws Exception {
		String stringValue = "string";

		StringBuffer builder = new StringBuffer();

		// header
		builder.append("stringValue");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append('"');
		builder.append(stringValue);
		builder.append('"');
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}
	
	@Test
	public void testEscapeWithDoubleQuoteCharacter() throws Exception {
		String originalValue = "string \"this\"";

		String stringValue = originalValue.replace("\"", "\"\"");
		
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("stringValue");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append('"');
		builder.append(stringValue);
		builder.append('"');
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(originalValue);
		
		assertThat(scanner.next()).isNull();
	}
	
	@Test
	public void testEscapeWithNewLineCharacter() throws Exception {
		String stringValue = "string \n with \n newline";
		
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("stringValue");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append('"');
		builder.append(stringValue);
		builder.append('"');
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}		
	
	@Test
	public void testNoEndQuoteThrowsException() throws Exception {
		String stringValue = "string \n with \n newline";
		
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("stringValue");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append('"');
		builder.append(stringValue);
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
				
		assertThrows(CsvException.class, () -> {
			scanner.next();
	    });

	}		
	
}
