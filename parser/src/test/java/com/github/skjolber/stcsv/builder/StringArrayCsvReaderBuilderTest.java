package com.github.skjolber.stcsv.builder;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.EmptyCsvReader;
import com.github.skjolber.stcsv.sa.DefaultStringArrayCsvReader;
import com.github.skjolber.stcsv.sa.NoLinebreakStringArrayCsvReader;
import com.github.skjolber.stcsv.sa.StringArrayCsvReader;
import com.github.skjolber.stcsv.sa.rfc4180.NoLinebreakRFC4180StringArrayCsvReader;

public class StringArrayCsvReaderBuilderTest {

	private String empty = "";
	private String singleLine = "a,b,c\n";
	private String quotedSingleLine = "\"a\",\"b\",\"c\"\n";
	private String indexes = "1,2,3\n";
	private String[] emptyColumns = {",,\n", "a,b,\n", "a,,\n"};

	@Test
	public void testConfiguration() throws Exception {
		StringArrayCsvReaderBuilder builder = StringArrayCsvReader.builder();
		
		builder.escapeCharacter('\\');
		assertThat(builder.getEscapeCharacter()).isEqualTo('\\');
		
		builder.divider(';');
		assertThat(builder.getDivider()).isEqualTo(';');

		builder.quoteCharacter('\'');
		assertThat(builder.getQuoteCharacter()).isEqualTo('\'');
	}
	
	@Test
	public void testEmpty() throws Exception {
		CsvReader<String[]> build = new StringArrayCsvReaderBuilder().build(new StringReader(empty));
		assertThat(build).isInstanceOf(EmptyCsvReader.class);
		assertThat(build.next()).isNull();
	}

	@Test
	public void testSingleLine() throws Exception {
		CsvReader<String[]> build = new StringArrayCsvReaderBuilder().build(new StringReader(singleLine));
		String[] next = build.next();
		assertThat(next[0]).isEqualTo("a");
		assertThat(next[1]).isEqualTo("b");
		assertThat(next[2]).isEqualTo("c");
		
		assertThat(build.next()).isNull();
	}

	@Test
	public void testEmptyFirstColumns() throws Exception {
		for(String empty : emptyColumns) {
			CsvReader<String[]> build = new StringArrayCsvReaderBuilder().build(new StringReader(empty + singleLine));
			build.next(); // skip first line

			String[] next = build.next();
			assertThat(next[0]).isEqualTo("a");
			assertThat(next[1]).isEqualTo("b");
			assertThat(next[2]).isEqualTo("c");
		}
	}
	
	@Test
	public void testFixedColumnIndexes() throws Exception {
		CsvReader<String[]> build = StringArrayCsvReader.builder().withColumnMapping("b", 0).build(new StringReader(singleLine + indexes));

		String[] next = build.next();
		assertThat(next.length).isEqualTo(1);
		assertThat(next[0]).isEqualTo("2");
		
		assertThat(build.next()).isNull();
	}
	
	@Test
	public void testFixedColumnIndexesWithQuotes() throws Exception {
		CsvReader<String[]> build = StringArrayCsvReader.builder().withColumnMapping("b", 0).build(new StringReader(quotedSingleLine + indexes));

		String[] next = build.next();
		assertThat(next.length).isEqualTo(1);
		assertThat(next[0]).isEqualTo("2");
		
		assertThat(build.next()).isNull();
	}	
	
	@Test
	public void testNoLinebreaks() throws Exception {
		CsvReader<String[]> build = StringArrayCsvReader.builder().quotedWithoutLinebreaks().build(new StringReader(quotedSingleLine + indexes));
		assertThat(build).isInstanceOf(NoLinebreakRFC4180StringArrayCsvReader.class);
	}		
	
	@Test
	public void testNoLinebreak() throws Exception {
		CsvReader<String[]> build = StringArrayCsvReader.builder().quotedWithoutLinebreaks().divider(';').escapeCharacter('\\').build(new StringReader(singleLine + indexes));
		assertThat(build).isInstanceOf(NoLinebreakStringArrayCsvReader.class);
	}
	
	@Test
	public void testCustomSeperators() throws Exception {
		CsvReader<String[]> build = StringArrayCsvReader.builder().divider(';').escapeCharacter('\\').build(new StringReader(singleLine + indexes));
		assertThat(build).isInstanceOf(DefaultStringArrayCsvReader.class);
	}

	@Test
	public void throwsExceptionUnsupportedSeperator() throws Exception {
		String bridge = "\ud83c\udf09"; // https://stackoverflow.com/questions/5903008/what-is-a-surrogate-pair-in-java
		assertThrows(CsvBuilderException.class, ()->{
			StringArrayCsvReader.builder().divider(bridge.charAt(1)).escapeCharacter('\\').build(new StringReader(singleLine + indexes));
	    });
	}
	
	@Test
	public void throwsExceptionForIllegalConfiguration() throws Exception {
		assertThrows(CsvBuilderException.class, ()->{
			StringArrayCsvReader.builder().escapeCharacter('\\').quoteCharacter('\\').build(new StringReader(singleLine + indexes));
	    });
		assertThrows(CsvBuilderException.class, ()->{
			StringArrayCsvReader.builder().skipComments().build(new StringReader(singleLine + indexes));
	    });
		assertThrows(CsvBuilderException.class, ()->{
			StringArrayCsvReader.builder().skipEmptyLines().build(new StringReader(singleLine + indexes));
	    });
	}		
}
