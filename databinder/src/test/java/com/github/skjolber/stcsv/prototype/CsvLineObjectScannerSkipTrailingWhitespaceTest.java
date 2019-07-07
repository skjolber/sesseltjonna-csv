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

public class CsvLineObjectScannerSkipTrailingWhitespaceTest {

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
				.booleanField("b")
					.consumer(CsvLineObject::setBooleanValue)
					.trimTrailingWhitespaces()
					.required()
				.longField("c")
					.consumer(CsvLineObject::setLongValue)
					.trimTrailingWhitespaces()
					.required()
				.integerField("d")
					.consumer(CsvLineObject::setIntegerValue)
					.trimTrailingWhitespaces()
					.required()
				.doubleField("e")
					.consumer(CsvLineObject::setDoubleValue)
					.trimTrailingWhitespaces()
					.required()
				.field("f")
					.consumer((a, b, c, d) -> {
						a.setFloatValue(Float.parseFloat(new String(b, c, d - c)));
					})
					.trimTrailingWhitespaces()
					.optional()
				.build();
		
		mappingWithQuotes = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.trimTrailingWhitespaces()
					.quoted()
					.required()
				.booleanField("b")
					.consumer(CsvLineObject::setBooleanValue)
					.quoted()
					.trimTrailingWhitespaces()
					.required()
				.longField("c")
					.consumer(CsvLineObject::setLongValue)
					.quoted()
					.trimTrailingWhitespaces()
					.required()
				.integerField("d")
					.consumer(CsvLineObject::setIntegerValue)
					.quoted()
					.trimTrailingWhitespaces()
					.required()
				.doubleField("e")
					.consumer(CsvLineObject::setDoubleValue)
					.quoted()
					.trimTrailingWhitespaces()
					.required()
				.field("f")
					.consumer((a, b, c, d) -> {
						a.setFloatValue(Float.parseFloat(new String(b, c, d - c)));
					})
					.quoted()
					.trimTrailingWhitespaces()
					.optional()
				.build();
		
 
	}


	@Test
	public void testSkipTrailingWhitespace() throws Exception {
		Reader reader = createReader(false, false, true);
		
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
	public void testSkipTrailingWhitespaceWithQuotes() throws Exception {
		Reader reader = createReader(true, false, true);
		
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
	
	public static Reader createReader(boolean quoted, boolean whitespaceBefore, boolean whitespaceAfter) {
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
		
		append(builder, "stringValue", quoted, whitespaceBefore, whitespaceAfter);
		append(builder, "false", quoted, whitespaceBefore, whitespaceAfter);
		append(builder, "1", quoted, whitespaceBefore, whitespaceAfter);
		append(builder, "1", quoted, whitespaceBefore, whitespaceAfter);
		append(builder, "1.0", quoted, whitespaceBefore, whitespaceAfter);
		append(builder, "2.1", quoted, whitespaceBefore, whitespaceAfter);
		
		append(builder, "random data", quoted, whitespaceBefore, whitespaceAfter);

		builder.setLength(builder.length() - 1);

		return new StringReader(builder.toString());
	}
	
	private static void append(StringBuffer builder, String value, boolean quoted, boolean whitespaceBefore, boolean whitespaceAfter) {
		if(quoted) {
			builder.append('"');
		}
		if(whitespaceBefore) {
			builder.append("  ");
		}
		builder.append(value);
		if(whitespaceAfter) {
			builder.append("  ");
		}
		if(quoted) {
			builder.append('"');
		}
		builder.append(',');
	}
	
}
