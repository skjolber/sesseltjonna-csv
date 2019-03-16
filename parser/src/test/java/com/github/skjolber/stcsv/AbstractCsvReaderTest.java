package com.github.skjolber.stcsv;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class AbstractCsvReaderTest {

	protected File file = new File("src/test/resources/gtfs/trips-plain-5000.txt");
	protected File quotedFile = new File("src/test/resources/gtfs/trips-quoted-5000.txt");
	protected File fileCarriageReturnNewline = new File("src/test/resources/gtfs/trips-plain-crln-5000.txt");
	protected File quotedFileCarriageReturnNewline = new File("src/test/resources/gtfs/trips-quoted-crln-5000.txt");

	protected void compare(CsvParser referenceParser, CsvReader<String[]> factory) throws Exception {
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
