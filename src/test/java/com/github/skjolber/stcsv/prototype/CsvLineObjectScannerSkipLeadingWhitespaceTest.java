package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvClassFactory;
import com.github.skjolber.stcsv.CsvClassMapping;
import com.github.skjolber.stcsv.CsvMappingException;

public class CsvLineObjectScannerSkipLeadingWhitespaceTest {

	private CsvClassMapping<CsvLineObject> mapping;

	@BeforeEach
	public void init() throws Exception {
		mapping = CsvClassMapping.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.trimLeadingWhitespaces()
					.required()
				.build();
	}
	
	@Test
	public void testSkipLeadingWhitespace() throws Exception {
		String stringValue = "string";
		
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("a");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append("  ");
		builder.append(stringValue);
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		CsvClassFactory<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
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

		CsvClassFactory<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		assertThrows(CsvMappingException.class, () -> {
			scanner.next();
	    });
	}	
}
