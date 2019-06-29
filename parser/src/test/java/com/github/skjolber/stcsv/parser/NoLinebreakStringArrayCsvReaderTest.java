package com.github.skjolber.stcsv.parser;

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
import com.github.skjolber.stcsv.sa.NoLinebreakStringArrayCsvReader;
import com.github.skjolber.stcsv.sa.rfc4180.RFC4180StringArrayCsvReader;
import com.univocity.parsers.csv.CsvParser;

public class NoLinebreakStringArrayCsvReaderTest extends AbstractCsvReaderTest {

	@Test
	public void compareToConventionalParserWithoutQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(semicolonFile, StandardCharsets.UTF_8, false, '"', '\\', ';');
		NoLinebreakStringArrayCsvReader factory = parser(semicolonFile, StandardCharsets.UTF_8, false);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserWithoutQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(semicolonFile, StandardCharsets.UTF_8, true, '"', '\\', ';');
		NoLinebreakStringArrayCsvReader factory = parser(semicolonFile, StandardCharsets.UTF_8, true);
		
		compare(referenceParser, factory);
	}
	
	@Test
	public void compareToConventionalParserWithQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(quotedSemicolonFile, StandardCharsets.UTF_8, false, '"', '\\', ';');
		NoLinebreakStringArrayCsvReader factory = parser(quotedSemicolonFile, StandardCharsets.UTF_8, false);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(quotedSemicolonFile, StandardCharsets.UTF_8, true, '"', '\\', ';');
		NoLinebreakStringArrayCsvReader factory = parser(quotedSemicolonFile, StandardCharsets.UTF_8, true);

		compare(referenceParser, factory);
	}
	
	public NoLinebreakStringArrayCsvReader parser(File file, Charset charset) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		
		return new NoLinebreakStringArrayCsvReader(reader1, 7, '"', '\\', ';');
	}	
	
	@Test
	public void throwsExceptionOnIncorrectColumns() throws Exception {
		String input = "a,b,c\n";
		NoLinebreakStringArrayCsvReader reader = new NoLinebreakStringArrayCsvReader(new StringReader(input), 5, '"', '\\', ',');
		
		Throwable exception = assertThrows(CsvException.class, ()->{
	            	reader.next();
	            } );
		assertThat(exception.getCause()).isInstanceOf(ArrayIndexOutOfBoundsException.class);
	}

	public NoLinebreakStringArrayCsvReader parser(File file, Charset charset, boolean crnl) throws Exception {
		InputStream input = new FileInputStream(file);
		
		Reader reader = new InputStreamReader(input, charset);
		if(crnl) {
			reader = new CarriageReturnNewLineReader(reader);
		}
		
		return new NoLinebreakStringArrayCsvReader(reader, 7, '"', '\\', ';');
	}
	
	@Test
	public void parsesEscapedInput() throws Exception {
		String input = "a,b,c\n\"a\\\"1\",b1,c1";
		NoLinebreakStringArrayCsvReader reader = new NoLinebreakStringArrayCsvReader(new StringReader(input),  3, '"', '\\', ',');
		
		String[] first = reader.next();
		assertThat(first[0]).isEqualTo("a");
		assertThat(first[1]).isEqualTo("b");
		assertThat(first[2]).isEqualTo("c");
		String[] second = reader.next();
		assertThat(second[0]).isEqualTo("a\"1");
		assertThat(second[1]).isEqualTo("b1");
		assertThat(second[2]).isEqualTo("c1");
	}	
}
