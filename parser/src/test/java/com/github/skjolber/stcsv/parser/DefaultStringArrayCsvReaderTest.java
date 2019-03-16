package com.github.skjolber.stcsv.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.AbstractCsvReaderTest;
import com.github.skjolber.stcsv.sa.DefaultStringArrayCsvReader;
import com.univocity.parsers.csv.CsvParser;

public class DefaultStringArrayCsvReaderTest extends AbstractCsvReaderTest {

	@Test
	public void compareToConventionalParserWithoutQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(file, StandardCharsets.UTF_8);
		DefaultStringArrayCsvReader factory = parser(file, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserWithoutQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(fileCarriageReturnNewline, StandardCharsets.UTF_8);
		DefaultStringArrayCsvReader factory = parser(fileCarriageReturnNewline, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}
	
	@Test
	public void compareToConventionalParserWithQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(quotedFile, StandardCharsets.UTF_8);
		DefaultStringArrayCsvReader factory = parser(quotedFile, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(quotedFileCarriageReturnNewline, StandardCharsets.UTF_8);
		DefaultStringArrayCsvReader factory = parser(quotedFileCarriageReturnNewline, StandardCharsets.UTF_8);

		compare(referenceParser, factory);
	}
	
	public DefaultStringArrayCsvReader parser(File file, Charset charset) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		
		return new DefaultStringArrayCsvReader(reader1, 7, '"', '"', ',');
	}	
}
