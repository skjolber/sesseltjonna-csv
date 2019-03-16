package com.github.skjolber.stcsv.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.AbstractCsvReaderTest;
import com.github.skjolber.stcsv.sa.NoLinebreakStringArrayCsvReader;
import com.univocity.parsers.csv.CsvParser;

public class NoLinebreakStringArrayCsvReaderTest extends AbstractCsvReaderTest {

	@Test
	public void compareToConventionalParserWithoutQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(file, StandardCharsets.UTF_8);
		NoLinebreakStringArrayCsvReader factory = parser(file, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserWithoutQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(fileCarriageReturnNewline, StandardCharsets.UTF_8);
		NoLinebreakStringArrayCsvReader factory = parser(fileCarriageReturnNewline, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}
	
	@Test
	public void compareToConventionalParserWithQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(quotedFile, StandardCharsets.UTF_8);
		NoLinebreakStringArrayCsvReader factory = parser(quotedFile, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(quotedFileCarriageReturnNewline, StandardCharsets.UTF_8);
		NoLinebreakStringArrayCsvReader factory = parser(quotedFileCarriageReturnNewline, StandardCharsets.UTF_8);

		compare(referenceParser, factory);
	}
	
	public NoLinebreakStringArrayCsvReader parser(File file, Charset charset) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		
		return new NoLinebreakStringArrayCsvReader(reader1, 7, '"', '"', ',');
	}	
}
