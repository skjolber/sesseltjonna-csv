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

public class CsvLineObjectScannerRequiredFieldTest {

	private CsvMapper<CsvLineObject> required;
	private CsvMapper<CsvLineObject> requiredWithConsumer;
	private CsvMapper<CsvLineObject> requiredQuotedWithoutNewlines;

	private CsvMapper<CsvLineObject> optional;
	private CsvMapper<CsvLineObject> optionalWithConsumer;
	private CsvMapper<CsvLineObject> optionalQuotedWithoutNewlines;

	@BeforeEach
	public void init() throws Exception {
		required = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.quoted()
					.required()
				.build();
		
		requiredWithConsumer = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.consumer(CsvLineObject::setStringValue)
					.quoted()
					.required()
				.build();
		
		requiredQuotedWithoutNewlines = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.quotedWithoutLinebreaks()
					.required()
				.build();
		
		optional = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.quoted()
					.optional()
				.build();		

		optionalWithConsumer = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.consumer(CsvLineObject::setStringValue)
					.quoted()
					.optional()
				.build();	
		
		optionalQuotedWithoutNewlines = CsvMapper.builder(CsvLineObject.class)
				.stringField("stringValue")
					.quotedWithoutLinebreaks()
					.optional()
				.build();		
		
	}
	
	@Test
	public void testValue() throws Exception {
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

		CsvReader<CsvLineObject> scanner = required.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}

	@Test
	public void testValueForConsumer() throws Exception {
		String stringValue = "string\"\"value";
		
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

		CsvReader<CsvLineObject> scanner = requiredWithConsumer.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue.replace("\"\"", "\""));
		
		assertThat(scanner.next()).isNull();
	}

	@Test
	public void testEmptyQuotes() throws Exception {
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("stringValue");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line (empty quotes)
		builder.append('"');
		builder.append('"');
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		assertThrows(CsvException.class, () -> {
			required.create(new StringReader(builder.toString())).next();
	    });

		assertThrows(CsvException.class, () -> {
			requiredWithConsumer.create(new StringReader(builder.toString())).next();
	    });

		assertThrows(CsvException.class, () -> {
			requiredQuotedWithoutNewlines.create(new StringReader(builder.toString())).next();
	    });
		
		optional.create(new StringReader(builder.toString())).next();
		optionalWithConsumer.create(new StringReader(builder.toString())).next();
		optionalQuotedWithoutNewlines.create(new StringReader(builder.toString())).next();
	}
	
	@Test
	public void testMissingRequiredValueThrowsExceptionOnComma() throws Exception {
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

		CsvReader<CsvLineObject> scanner = required.create(new StringReader(builder.toString()));
		
		assertThrows(CsvException.class, () -> {
			scanner.next();
	    });
	}
	
	@Test
	public void testValueWithNewlines() throws Exception {
		String stringValue = "string\n\nvalue";
		
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

		CsvReader<CsvLineObject> scanner = required.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}
	
	@Test
	public void testQuotesWithoutNewlines() throws Exception {
		String stringValue = "string \"\" value";
		
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

		CsvReader<CsvLineObject> scanner = requiredQuotedWithoutNewlines.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue.replace("\"\"", "\""));
		
		assertThat(scanner.next()).isNull();
	}
	
}
