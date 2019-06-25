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
import com.github.skjolber.stcsv.sa.DefaultStringArrayCsvReader;
import com.univocity.parsers.csv.CsvParser;

public class DefaultStringArrayCsvReaderTest extends AbstractCsvReaderTest {

	@Test
	public void compareToConventionalParserWithoutQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(semicolonFile, StandardCharsets.UTF_8, true, '"', '\\', ';');
		DefaultStringArrayCsvReader factory = parser(semicolonFile, StandardCharsets.UTF_8, false);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserWithoutQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(semicolonFile, StandardCharsets.UTF_8, true, '"', '\\', ';');
		DefaultStringArrayCsvReader factory = parser(semicolonFile, StandardCharsets.UTF_8, true);
		
		compare(referenceParser, factory);
	}
	
	@Test
	public void compareToConventionalParserWithQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(quotedSemicolonFile, StandardCharsets.UTF_8, true, '"', '\\', ';');
		DefaultStringArrayCsvReader factory = parser(quotedSemicolonFile, StandardCharsets.UTF_8, false);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(quotedSemicolonFile, StandardCharsets.UTF_8, true, '"', '\\', ';');
		DefaultStringArrayCsvReader factory = parser(quotedSemicolonFile, StandardCharsets.UTF_8, true);

		compare(referenceParser, factory);
	}
	
	@Test
	public void throwsExceptionOnGarbageInput() throws Exception {
		String input = "a,b,c\n";
		DefaultStringArrayCsvReader reader = new DefaultStringArrayCsvReader(new StringReader(input), 5, '"', '\\', ';');
		
		Throwable exception = assertThrows(CsvException.class, ()->{
	            	reader.next();
	            } );
		assertThat(exception.getCause()).isInstanceOf(ArrayIndexOutOfBoundsException.class);
	}	
	
	public DefaultStringArrayCsvReader parser(File file, Charset charset, boolean crnl) throws Exception {
		InputStream input = new FileInputStream(file);
		
		Reader reader = new InputStreamReader(input, charset);
		if(crnl) {
			reader = new CarriageReturnNewLineReader(reader);
		}
		
		return new DefaultStringArrayCsvReader(reader, 7, '"', '\\', ';');
	}	
}
