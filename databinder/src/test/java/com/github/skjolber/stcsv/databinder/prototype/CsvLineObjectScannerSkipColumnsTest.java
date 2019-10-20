package com.github.skjolber.stcsv.databinder.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.databinder.CsvMapper;

public class CsvLineObjectScannerSkipColumnsTest {

	@Test
	public void testSkipLastColumnNewLine() throws Exception {
		
		String stringValue = "string";

		CsvMapper<CsvLineObject> mapping = CsvMapper.builder(CsvLineObject.class)
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

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}

	@Test
	public void testSkipLastColumnNewLineCarriageReturn() throws Exception {
		
		String stringValue = "string";

		CsvMapper<CsvLineObject> mapping = CsvMapper.builder(CsvLineObject.class)
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

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}
	
	@Test
	public void testSkipFirstMiddleColumnWithoutLinebreaksNewLine() throws Exception {
		
		String stringValue = "string";

		CsvMapper<CsvLineObject> mapping = CsvMapper.builder(CsvLineObject.class)
				.skippableFieldsWithoutLinebreaks()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.required()
				.build();

		StringBuffer builder = new StringBuffer();
		
		// header
		builder.append("randomColumn1");
		builder.append(",");
		builder.append("a");
		builder.append(",");
		builder.append("randomColumn2");
		builder.append("\n");
		
		// first line
		builder.append("random data 1");
		builder.append(",");
		builder.append(stringValue);
		builder.append(",");
		builder.append("random data 2");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}

	@Test
	public void testSkipFirstMiddleColumnWithLinebreaksNewLine() throws Exception {
		
		String stringValue = "string";

		CsvMapper<CsvLineObject> mapping = CsvMapper.builder(CsvLineObject.class)
				.skippableFieldsWithoutLinebreaks()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.required()
				.build();

		StringBuffer builder = new StringBuffer();
		
		// header
		builder.append("randomColumn1");
		builder.append(",");
		builder.append("a");
		builder.append(",");
		builder.append("randomColumn2");
		builder.append("\n");
		
		// first line
		builder.append("\"random\n data 1\"");
		builder.append(",");
		builder.append(stringValue);
		builder.append(",");
		builder.append("\"random\n data 2\"");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		
		assertThat(scanner.next()).isNull();
	}
	


}
