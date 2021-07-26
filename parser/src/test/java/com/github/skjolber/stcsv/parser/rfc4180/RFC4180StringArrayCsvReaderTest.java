package com.github.skjolber.stcsv.parser.rfc4180;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.AbstractCsvTest;
import com.github.skjolber.stcsv.CarriageReturnNewLineReader;
import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.sa.rfc4180.RFC4180StringArrayCsvReader;
import com.univocity.parsers.csv.CsvParser;

public class RFC4180StringArrayCsvReaderTest extends AbstractCsvTest {

	@Test
	public void compareToConventionalParserWithoutQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(file, StandardCharsets.UTF_8, false);
		RFC4180StringArrayCsvReader factory = parser(file, StandardCharsets.UTF_8, false);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserWithoutQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(file, StandardCharsets.UTF_8, true);
		RFC4180StringArrayCsvReader factory = parser(file, StandardCharsets.UTF_8, true);
		
		compare(referenceParser, factory);
	}
	
	@Test
	public void compareToConventionalParserWithQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(quotedFile, StandardCharsets.UTF_8, false);
		RFC4180StringArrayCsvReader factory = parser(quotedFile, StandardCharsets.UTF_8, false);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(quotedFile, StandardCharsets.UTF_8, true);
		RFC4180StringArrayCsvReader factory = parser(quotedFile, StandardCharsets.UTF_8, true);

		compare(referenceParser, factory);
	}
	
	@Test
	public void throwsExceptionOnIncorrectColumns() throws Exception {
		String input = "a,b,c\n";
		RFC4180StringArrayCsvReader reader = new RFC4180StringArrayCsvReader(new StringReader(input), 5);
		
		Throwable exception = assertThrows(CsvException.class, ()->{
	            	reader.next();
	            } );
		assertThat(exception.getCause()).isInstanceOf(ArrayIndexOutOfBoundsException.class);
	}
	
	public RFC4180StringArrayCsvReader parser(File file, Charset charset, boolean crnl) throws Exception {
		InputStream input = new FileInputStream(file);
		
		Reader reader = new InputStreamReader(input, charset);
		if(crnl) {
			reader = new CarriageReturnNewLineReader(reader);
		}
		
		return new RFC4180StringArrayCsvReader(reader, 7);
	}	
	
	@Test
	public void parseQuotedNewlineWithFillBufferFirstOrMiddleColumn() throws Exception {
		String row = "abcdef,\"b1\nb2b3b4\",end\n";

		int index = row.indexOf('\n');

		char[] first = row.substring(0, index+1).toCharArray();
		
		char[] b = new char[1024];
		System.arraycopy(first, 0, b, 0, first.length);
		
		RFC4180StringArrayCsvReader reader = new RFC4180StringArrayCsvReader(new StringReader(row.substring(index + 1)), b, 0, first.length, 3);
		
		String[] result = reader.next();
		assertThat(result[0]).isEqualTo("abcdef");
		assertThat(result[1]).isEqualTo("b1\nb2b3b4");
		assertThat(result[2]).isEqualTo("end");
	}
	
	@Test
	public void parseQuotedNewlineWithFillBufferEndColumn() throws Exception {
		String row = "abcdef,ghijk,\"b1\nb2b3b4\"\n";

		int index = row.indexOf('\n');

		char[] first = row.substring(0, index+1).toCharArray();
		
		char[] b = new char[1024];
		System.arraycopy(first, 0, b, 0, first.length);
		
		RFC4180StringArrayCsvReader reader = new RFC4180StringArrayCsvReader(new StringReader(row.substring(index + 1)), b, 0, first.length, 3);
		
		String[] result = reader.next();
		assertThat(result[0]).isEqualTo("abcdef");
		assertThat(result[1]).isEqualTo("ghijk");
		assertThat(result[2]).isEqualTo("b1\nb2b3b4");
	}

	@Test
	public void parsesEscapedInput() throws Exception {
		String input = "a,b,c\n\"a\"\"1\",b1,\"c1\"\"\"";
		RFC4180StringArrayCsvReader reader = new RFC4180StringArrayCsvReader(new StringReader(input), 3);
		
		String[] first = reader.next();
		assertThat(first[0]).isEqualTo("a");
		assertThat(first[1]).isEqualTo("b");
		assertThat(first[2]).isEqualTo("c");
		String[] second = reader.next();
		assertThat(second[0]).isEqualTo("a\"1");
		assertThat(second[1]).isEqualTo("b1");
		assertThat(second[2]).isEqualTo("c1\"");
	}
	
	@Test
	public void handlesWhitespaceAfterQuotes() throws Exception {
		String row = "a,\"123\" ,end\n";

		RFC4180StringArrayCsvReader reader = new RFC4180StringArrayCsvReader(new StringReader(row), 3);
		
		String[] result = reader.next();
		assertThat(result[0]).isEqualTo("a");
		assertThat(result[1]).isEqualTo("123");
		assertThat(result[2]).isEqualTo("end");
	}

	@Test
	public void throwsExceptionWhenReaderRunsEmptyMiddleColumnNonQuoted() throws Exception {
		String row = "abcdef,\"b1\nb2b3b4\",end\n";

		int index = row.indexOf('\n');

		char[] first = row.substring(0, index+1).toCharArray();
		
		char[] b = new char[1024];
		System.arraycopy(first, 0, b, 0, first.length);
		
		RFC4180StringArrayCsvReader reader = new RFC4180StringArrayCsvReader(new StringReader(""), b, 0, first.length, 3);

		assertThrows(CsvException.class, ()->{
        	reader.next();
        } );

	}

	@Test
	public void throwsExceptionWhenReaderRunsEmptyMiddleColumnQuoted() throws Exception {
		String row = "abcdef,\"b1b2b3b4\"\n,end\n";

		int index = row.indexOf('\n');

		char[] first = row.substring(0, index+1).toCharArray();
		
		char[] b = new char[1024];
		System.arraycopy(first, 0, b, 0, first.length);
		
		RFC4180StringArrayCsvReader reader = new RFC4180StringArrayCsvReader(new StringReader(""), b, 0, first.length, 3);

		assertThrows(CsvException.class, ()->{
        	reader.next();
        } );

	}

	@Test
	public void throwsExceptionWhenReaderRunsEmptyEndColumnNonQuotes() throws Exception {
		String row = "abcdef,ghijk,\"b1\nb2b3b4\"\n";

		int index = row.indexOf('\n');

		char[] first = row.substring(0, index+1).toCharArray();
		
		char[] b = new char[1024];
		System.arraycopy(first, 0, b, 0, first.length);
		
		RFC4180StringArrayCsvReader reader = new RFC4180StringArrayCsvReader(new StringReader(""), b, 0, first.length, 3);

		assertThrows(CsvException.class, ()->{
        	reader.next();
        } );

	}
	
	
}
