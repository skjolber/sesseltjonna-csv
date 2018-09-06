package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvClassFactory;
import com.github.skjolber.stcsv.CsvClassMapping;

public class CsvLineObjectScannerSkipEndOfLineTest {

	@Test
	public void testSkipEndOfLineNewLine() throws Exception {
		
		String stringValue = "string";

		CsvClassMapping<CsvLineObject> mapping = CsvClassMapping.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.required()
				.build();

		StringBuffer builder = new StringBuffer();
		
		// header
		builder.append("a");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
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
	public void testSkipEndOfLineNewLineCarriageReturn() throws Exception {
		
		String stringValue = "string";

		CsvClassMapping<CsvLineObject> mapping = CsvClassMapping.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.required()
				.build();

		StringBuffer builder = new StringBuffer();
		
		// header
		builder.append("a");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\r\n");
		
		// first line
		builder.append(stringValue);
		builder.append(",");
		builder.append("random data");
		builder.append("\r\n");

		CsvClassFactory<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}

}
