package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvClassFactory;
import com.github.skjolber.stcsv.CsvClassMapping;
import com.github.skjolber.stcsv.CsvMappingException;

public class CsvLineObjectScannerRequiredFieldTest {

	private CsvClassMapping<CsvLineObject> required;
	private CsvClassMapping<CsvLineObject> requiredWithConsumer;
	private CsvClassMapping<CsvLineObject> requiredQuotedWithoutNewlines;

	private CsvClassMapping<CsvLineObject> optional;
	private CsvClassMapping<CsvLineObject> optionalWithConsumer;
	private CsvClassMapping<CsvLineObject> optionalQuotedWithoutNewlines;

	@BeforeEach
	public void init() throws Exception {
		required = CsvClassMapping.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.quoted()
					.required()
				.build();
		
		requiredWithConsumer = CsvClassMapping.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.consumer(CsvLineObject::setStringValue)
					.quoted()
					.required()
				.build();
		
		requiredQuotedWithoutNewlines = CsvClassMapping.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.quotedWithoutLinebreaks()
					.required()
				.build();
		
		optional = CsvClassMapping.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.quoted()
					.optional()
				.build();		

		optionalWithConsumer = CsvClassMapping.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("stringValue")
					.consumer(CsvLineObject::setStringValue)
					.quoted()
					.optional()
				.build();	
		
		optionalQuotedWithoutNewlines = CsvClassMapping.builder(CsvLineObject.class)
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

		CsvClassFactory<CsvLineObject> scanner = required.create(new StringReader(builder.toString()));
		
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

		CsvClassFactory<CsvLineObject> scanner = requiredWithConsumer.create(new StringReader(builder.toString()));
		
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

		assertThrows(CsvMappingException.class, () -> {
			required.create(new StringReader(builder.toString())).next();
	    });

		assertThrows(CsvMappingException.class, () -> {
			requiredWithConsumer.create(new StringReader(builder.toString())).next();
	    });

		assertThrows(CsvMappingException.class, () -> {
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

		CsvClassFactory<CsvLineObject> scanner = required.create(new StringReader(builder.toString()));
		
		assertThrows(CsvMappingException.class, () -> {
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

		CsvClassFactory<CsvLineObject> scanner = required.create(new StringReader(builder.toString()));
		
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

		CsvClassFactory<CsvLineObject> scanner = requiredQuotedWithoutNewlines.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue.replace("\"\"", "\""));
		
		assertThat(scanner.next()).isNull();
	}
	
}
