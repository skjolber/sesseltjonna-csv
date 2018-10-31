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

	private CsvMapper<CsvLineObject> mapping1;
	private CsvMapper<CsvLineObject> mapping2;
	private CsvMapper<CsvLineObject> mapping3;
	private CsvMapper<CsvLineObject> mapping4;
	
	@BeforeEach
	public void init() throws Exception {
		mapping1 = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.quoted()
					.required()
				.build();
		mapping2 = CsvMapper.builder(CsvLineObject.class)
				.quoteCharacter('\'')
				.skipEmptyLines()
				.stringField("stringValue")
					.quoted()
					.required()
				.build();
		
		mapping3 = CsvMapper.builder(CsvLineObject.class)
				.quoteCharacter('\'')
				.skipEmptyLines()
				.stringField("stringValue")
					.quoted()
					.optional()
				.build();		
		mapping4 = CsvMapper.builder(CsvLineObject.class)
				.quoteCharacter('\'')
				.escapeCharacter('\\')
				.skipEmptyLines()
				.stringField("stringValue")
					.quoted()
					.optional()
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

		CsvReader<CsvLineObject> scanner = mapping1.create(new StringReader(builder.toString()));
		
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

		CsvReader<CsvLineObject> scanner = mapping1.create(new StringReader(builder.toString()));
		
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

		CsvReader<CsvLineObject> scanner = mapping1.create(new StringReader(builder.toString()));
		
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

		CsvReader<CsvLineObject> scanner = mapping1.create(new StringReader(builder.toString()));
				
		assertThrows(CsvException.class, () -> {
			scanner.next();
	    });

	}		
	
	
	@Test
	public void testEscapeCustomQuoteCharacterRequired() throws Exception {
		String stringValue = "string";

		StringBuffer builder = new StringBuffer();

		// header
		builder.append("stringValue");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append("'");
		builder.append(stringValue);
		builder.append("'");
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping2.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}
		
	@Test
	public void testEscapeCustomQuoteCharacterOptional() throws Exception {
		StringBuffer builder = new StringBuffer();

		// header
		builder.append("stringValue");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping3.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isNull();
		
		assertThat(scanner.next()).isNull();
	}
	
	@Test
	public void testEscapeWithSlash() throws Exception {
		String originalValue = "string \\this\\";

		String stringValue = originalValue.replace("\\", "\\\\");
		
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("stringValue");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append('\'');
		builder.append(stringValue);
		builder.append('\'');
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping4.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(originalValue);
		
		assertThat(scanner.next()).isNull();
	}	
}
