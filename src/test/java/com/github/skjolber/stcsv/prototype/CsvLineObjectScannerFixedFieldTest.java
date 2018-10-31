package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.CsvException;

public class CsvLineObjectScannerFixedFieldTest {

	private CsvMapper<CsvLineObject> mapping;
	private CsvMapper<CsvLineObject> mappingWithQuotes;
	private CsvMapper<CsvLineObject> mappingWithQuotesAndCustomEscapes;
	
	@BeforeEach
	public void init() throws Exception {
		mapping = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.fixedSize(2)
					.required()
				.build();
		
		mappingWithQuotes = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.fixedSize(2)
					.quoted()
					.required()
				.build();
		
		mappingWithQuotesAndCustomEscapes = CsvMapper.builder(CsvLineObject.class)
				.skipEmptyLines()
				.quoteCharacter('\'')
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.fixedSize(2)
					.quoted()
					.required()
				.build();			
	}
	
	@Test
	public void testValueOfCorrectSizeValue() throws Exception {
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("a");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append("bb");
		builder.append(",");
		builder.append("random data");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo("bb");
		
		assertThat(scanner.next()).isNull();
	}
	
	@Test
	public void testMissingValueThrowsException() throws Exception {
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("a");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line (empty quotes)
		builder.append("");
		builder.append(",");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		assertThrows(CsvException.class, () -> {
			scanner.next();
	    });
	}

	@Test
	public void testValueOfCorrectSizeValueNewline() throws Exception {
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("a");
		builder.append("\n");
		
		// first line
		builder.append("bb");
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mapping.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo("bb");
		
		assertThat(scanner.next()).isNull();
	}

	@Test
	public void testQuotedValueOfCorrectSizeValue() throws Exception {
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("a");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append('"');
		builder.append("bb");
		builder.append('"');
		builder.append(",");
		builder.append('"');
		builder.append("random data");
		builder.append('"');
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mappingWithQuotes.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo("bb");
		
		assertThat(scanner.next()).isNull();
	}
	
	@Test
	public void testQuotedValueWithSpecialSignsOfCorrectSizeValue() throws Exception {
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("a");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append('"');
		builder.append("\"\"\n");
		builder.append('"');
		builder.append(",");
		builder.append('"');
		builder.append("random data");
		builder.append('"');
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mappingWithQuotes.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo("\"\n");
		
		assertThat(scanner.next()).isNull();
	}
	

	@Test
	public void testQuotedValueOfCorrectSizeValueCustomQuotes() throws Exception {
		StringBuffer builder = new StringBuffer();
		// header
		builder.append("a");
		builder.append(",");
		builder.append("randomValue");
		builder.append("\n");
		
		// first line
		builder.append('\'');
		builder.append("bb");
		builder.append('\'');
		builder.append(",");
		builder.append('\'');
		builder.append("random data");
		builder.append('\'');
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = mappingWithQuotesAndCustomEscapes.create(new StringReader(builder.toString()));
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo("bb");
		
		assertThat(scanner.next()).isNull();
	}	
	
}
