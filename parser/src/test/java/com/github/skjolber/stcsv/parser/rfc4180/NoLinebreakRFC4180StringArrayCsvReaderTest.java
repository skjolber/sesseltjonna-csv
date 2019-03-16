package com.github.skjolber.stcsv.parser.rfc4180;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.AbstractCsvReaderTest;
import com.github.skjolber.stcsv.sa.rfc4180.NoLinebreakRFC4180StringArrayCsvReader;
import com.univocity.parsers.csv.CsvParser;

public class NoLinebreakRFC4180StringArrayCsvReaderTest extends AbstractCsvReaderTest {

	@Test
	public void compareToConventionalParserWithoutQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(file, StandardCharsets.UTF_8);
		NoLinebreakRFC4180StringArrayCsvReader factory = parser(file, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserWithoutQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(fileCarriageReturnNewline, StandardCharsets.UTF_8);
		NoLinebreakRFC4180StringArrayCsvReader factory = parser(fileCarriageReturnNewline, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}
	
	@Test
	public void compareToConventionalParserWithQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(quotedFile, StandardCharsets.UTF_8);
		NoLinebreakRFC4180StringArrayCsvReader factory = parser(quotedFile, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(quotedFileCarriageReturnNewline, StandardCharsets.UTF_8);
		NoLinebreakRFC4180StringArrayCsvReader factory = parser(quotedFileCarriageReturnNewline, StandardCharsets.UTF_8);

		compare(referenceParser, factory);
	}
	
	public NoLinebreakRFC4180StringArrayCsvReader parser(File file, Charset charset) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		
		return new NoLinebreakRFC4180StringArrayCsvReader(reader1, 7);
	}	
}
