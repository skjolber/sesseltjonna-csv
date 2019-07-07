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

public class CsvLineObjectScannerSkipLeadingTrailingWhitespaceTest {

	private CsvMapper<CsvLineObject> mapping;
	private CsvMapper<CsvLineObject> mappingWithQuotes;
	
	@BeforeEach
	public void init() throws Exception {
		mapping = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.required()
				.booleanField("b")
					.consumer(CsvLineObject::setBooleanValue)
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.required()
				.longField("c")
					.consumer(CsvLineObject::setLongValue)
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.required()
				.integerField("d")
					.consumer(CsvLineObject::setIntegerValue)
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.required()
				.doubleField("e")
					.consumer(CsvLineObject::setDoubleValue)
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.required()
				.field("f")
					.consumer((a, b, c, d) -> {
						a.setFloatValue(Float.parseFloat(new String(b, c, d - c)));
					})
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.optional()
				.build();
		
		mappingWithQuotes = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.quoted()
					.required()
				.booleanField("b")
					.consumer(CsvLineObject::setBooleanValue)
					.quoted()
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.required()
				.longField("c")
					.consumer(CsvLineObject::setLongValue)
					.quoted()
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.required()
				.integerField("d")
					.consumer(CsvLineObject::setIntegerValue)
					.quoted()
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.required()
				.doubleField("e")
					.consumer(CsvLineObject::setDoubleValue)
					.quoted()
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.required()
				.field("f")
					.consumer((a, b, c, d) -> {
						a.setFloatValue(Float.parseFloat(new String(b, c, d - c)));
					})
					.trimTrailingWhitespaces()
					.trimLeadingWhitespaces()
					.quoted()
					.optional()
				.build();
		
	}

	@Test
	public void testSkipWhitespace() throws Exception {
		Reader reader = CsvLineObjectScannerSkipTrailingWhitespaceTest.createReader(false, true, true);
		
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
	public void testSkipWhitespaceWithQuotes() throws Exception {
		Reader reader = CsvLineObjectScannerSkipTrailingWhitespaceTest.createReader(true, true, true);
		
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
		builder.append("b");
		builder.append(",");
		builder.append("c");
		builder.append(",");
		builder.append("d");
		builder.append(",");
		builder.append("e");
		builder.append(",");
		builder.append("f");
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
