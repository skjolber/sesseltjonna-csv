package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.CsvReader;

public class CsvLineObjectScannerSkipLeadingWhitespaceTest {

	private CsvMapper<CsvLineObject> mapping;
	private CsvMapper<CsvLineObject> mappingWithQuotes;
	
	@BeforeEach
	public void init() throws Exception {
		mapping = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.trimLeadingWhitespaces()
					.required()
				.booleanField("b")
					.consumer(CsvLineObject::setBooleanValue)
					.trimLeadingWhitespaces()
					.optional()
				.longField("c")
					.consumer(CsvLineObject::setLongValue)
					.trimLeadingWhitespaces()
					.optional()
				.integerField("d")
					.consumer(CsvLineObject::setIntegerValue)
					.trimLeadingWhitespaces()
					.optional()
				.doubleField("e")
					.consumer(CsvLineObject::setDoubleValue)
					.trimLeadingWhitespaces()
					.optional()
				.field("f")
					.consumer((a, b, c, d) -> {
						a.setFloatValue(Float.parseFloat(new String(b, c, d - c)));
					})
					.trimLeadingWhitespaces()
					.optional()
					
				.build();
		
		mappingWithQuotes = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.trimLeadingWhitespaces()
					.quoted()
					.required()
				.booleanField("b")
					.consumer(CsvLineObject::setBooleanValue)
					.quoted()
					.trimLeadingWhitespaces()
					.optional()
				.longField("c")
					.consumer(CsvLineObject::setLongValue)
					.quoted()
					.trimLeadingWhitespaces()
					.optional()
				.integerField("d")
					.consumer(CsvLineObject::setIntegerValue)
					.quoted()
					.trimLeadingWhitespaces()
					.optional()
				.doubleField("e")
					.consumer(CsvLineObject::setDoubleValue)
					.quoted()
					.trimLeadingWhitespaces()
					.optional()
				.field("f")
					.consumer((a, b, c, d) -> {
						a.setFloatValue(Float.parseFloat(new String(b, c, d - c)));
					})
					.quoted()
					.trimLeadingWhitespaces()
					.optional()
					
				.build();
		
	}

	
	@Test
	public void testSkipLeadingWhitespace() throws Exception {
		
		Reader reader = CsvLineObjectScannerSkipTrailingWhitespaceTest.createReader(false, true, false);

		CsvReader<CsvLineObject> scanner = mapping.create(reader);
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo("stringValue");
		assertThat(next.getIntegerValue()).isEqualTo(1);
		assertThat(next.getLongValue()).isEqualTo(1L);
		assertThat(next.getBooleanValue()).isEqualTo(false);
		assertThat(next.getDoubleValue()).isEqualTo(1.0d);
		assertThat(next.getFloatValue()).isEqualTo(2.1f);

		assertThat(scanner.next()).isNull();
	}
	
	@Test
	public void testSkipLeadingWhitespaceWithQuotes() throws Exception {
		Reader reader = CsvLineObjectScannerSkipTrailingWhitespaceTest.createReader(true, true, false);
		
		CsvReader<CsvLineObject> scanner = mappingWithQuotes.create(reader);
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo("stringValue");
		assertThat(next.getIntegerValue()).isEqualTo(1);
		assertThat(next.getLongValue()).isEqualTo(1L);
		assertThat(next.getBooleanValue()).isEqualTo(false);
		assertThat(next.getDoubleValue()).isEqualTo(1.0d);
		assertThat(next.getFloatValue()).isEqualTo(2.1f);

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

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		assertThrows(CsvException.class, () -> {
			scanner.next();
	    });
	}	
}
