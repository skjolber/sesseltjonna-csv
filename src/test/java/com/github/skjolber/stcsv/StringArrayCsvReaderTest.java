package com.github.skjolber.stcsv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class StringArrayCsvReaderTest {

	private File file = new File("src/test/resources/gtfs/trips-plain-5000.txt");
	private File quotedFile = new File("src/test/resources/gtfs/trips-quoted-5000.txt");
	private File fileCarriageReturnNewline = new File("src/test/resources/gtfs/trips-plain-crln-5000.txt");
	private File quotedFileCarriageReturnNewline = new File("src/test/resources/gtfs/trips-quoted-crln-5000.txt");

	@Test
	public void compareToConventionalParserWithoutQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(file, StandardCharsets.UTF_8);
		StringArrayCsvReader factory = parser(file, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}

	@Test
	public void compareToConventionalParserWithoutQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(fileCarriageReturnNewline, StandardCharsets.UTF_8);
		StringArrayCsvReader factory = parser(fileCarriageReturnNewline, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}
	
	@Test
	public void compareToConventionalParserWithQuotes() throws Exception {
		CsvParser referenceParser = referenceParser(quotedFile, StandardCharsets.UTF_8);
		StringArrayCsvReader factory = parser(quotedFile, StandardCharsets.UTF_8);
		
		compare(referenceParser, factory);
	}

	private void compare(CsvParser referenceParser, StringArrayCsvReader factory) throws IOException {
		int count = 0;
		
		do {
			String[] trip = factory.next();
			if(trip == null) {
				break;
			}
		
			String[] row = referenceParser.parseNext();
			for(int i = 0; i < trip.length; i++) {
				if(!Objects.equals(trip[i], row[i])) {
					System.out.println("Line " + count + " column " + i + ": " + trip[i] + " vs " + row[i]);
				}
			}
			count++;
		} while(true);

		System.out.println("Parsed " + count + " lines");
	}

	@Test
	public void compareToConventionalParserQuotesAndCarriageReturn() throws Exception {
		CsvParser referenceParser = referenceParser(quotedFileCarriageReturnNewline, StandardCharsets.UTF_8);
		StringArrayCsvReader factory = parser(quotedFileCarriageReturnNewline, StandardCharsets.UTF_8);

		compare(referenceParser, factory);
	}
	
	public StringArrayCsvReader parser(File file, Charset charset) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		
		return new StringArrayCsvReader(reader1, 7);
	}
	
	public static CsvParser referenceParser(File file, Charset charset) throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setSkipEmptyLines(false);
		settings.setColumnReorderingEnabled(false);
		
		//##CODE_START

		// creates a CSV parser
		CsvParser parser = new CsvParser(settings);
		
		InputStream input = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(input, charset);
		
		parser.beginParsing(reader);		
		
		return parser;
	}	
}
