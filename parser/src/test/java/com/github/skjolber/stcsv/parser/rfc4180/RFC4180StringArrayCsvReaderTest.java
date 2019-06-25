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

import com.github.skjolber.stcsv.AbstractCsvReaderTest;
import com.github.skjolber.stcsv.CarriageReturnNewLineReader;
import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.sa.rfc4180.RFC4180StringArrayCsvReader;
import com.univocity.parsers.csv.CsvParser;

public class RFC4180StringArrayCsvReaderTest extends AbstractCsvReaderTest {

	@Test
	public void parsesEscapedInput() throws Exception {
		String input = "a,b,c\n\"a\"\"1\",b1,c1";
		RFC4180StringArrayCsvReader reader = new RFC4180StringArrayCsvReader(new StringReader(input), 3);
		
		String[] first = reader.next();
		assertThat(first[0]).isEqualTo("a");
		assertThat(first[1]).isEqualTo("b");
		assertThat(first[2]).isEqualTo("c");
		String[] second = reader.next();
		assertThat(second[0]).isEqualTo("a\"1");
		assertThat(second[1]).isEqualTo("b1");
		assertThat(second[2]).isEqualTo("c1");
	}
	
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
}
