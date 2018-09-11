package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.CsvException;

public class CsvLineObjectScannerSkipTrailingWhitespaceTest {

	private String stringValue = "string";
	
	private CsvMapper<CsvLineObject> mapping;
	private CsvMapper<CsvLineObject> mappingWithQuotes;
	
	@BeforeEach
	public void init() throws Exception {
		mapping = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.trimTrailingWhitespaces()
					.required()
				.build();
		
		mappingWithQuotes = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.trimTrailingWhitespaces()
					.quoted()
					.required()
				.build();
		
	}
	
	@Test
	public void testSkipTrailingWhitespace() throws Exception {
		Reader reader = createReader(false);
		
		CsvReader<CsvLineObject> scanner = mapping.create(reader);
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}

	@Test
	public void testSkipTrailingWhitespaceWithQuotes() throws Exception {
		Reader reader = createReader(true);
		char a = ',';
		CsvReader<CsvLineObject> scanner = mappingWithQuotes.create(reader);
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}	
	
	private Reader createReader(boolean quoted) {
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("a");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		if(quoted) {
			builder.append('"');
		}
		builder.append(stringValue);
		builder.append("  ");
		if(quoted) {
			builder.append('"');
		}
		builder.append(",");
		if(quoted) {
			builder.append('"');
		}
		builder.append("random data");
		if(quoted) {
			builder.append('"');
		}
		builder.append("\n");

		return new StringReader(builder.toString());
	}
	
	@Test
	public void testTrimsWhitespaceThrowsExceptionOnRequired() throws Exception {
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("a");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line (just whitespace value)
		builder.append("  ");
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		assertThrows(CsvException.class, () -> {
			scanner.next();
	    });
	}		
}
