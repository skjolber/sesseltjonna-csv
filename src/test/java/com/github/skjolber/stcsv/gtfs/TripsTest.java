package com.github.skjolber.stcsv.gtfs;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.CsvMapper;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
/**
 * 
 * https://developers.google.com/transit/gtfs/reference/#tripstxt
 *
 */
public class TripsTest {

	private File file = new File("src/test/resources/gtfs/trips-plain-5000.txt");
	private File quotedFile = new File("src/test/resources/gtfs/trips-quoted-5000.txt");

	private CsvMapper<Trip> plain;
	private CsvMapper<Trip> quoted;
	
	@BeforeEach
	public void init() throws Exception {
		
		plain = CsvMapper.builder(Trip.class)
				.stringField("route_id")
					.setter(Trip::setRouteId)
					.quoted()
					.optional()
				.stringField("service_id")
					.setter(Trip::setServiceId)
					.required()
				.stringField("trip_id")
					.setter(Trip::setTripId)
					.required()
				.stringField("trip_headsign")
					.setter(Trip::setTripHeadsign)
					.quoted()
					.optional()
				.integerField("direction_id")
					.setter(Trip::setDirectionId)
					.optional()
				.stringField("shape_id")
					.setter(Trip::setShapeId)
					.optional()
				.integerField("wheelchair_accessible")
					.setter(Trip::setWheelchairAccessible)
					.optional()
				.build();		
		
		quoted = CsvMapper.builder(Trip.class)
				.stringField("route_id")
					.setter(Trip::setRouteId)
					.quoted()
					.optional()
				.stringField("service_id")
					.setter(Trip::setServiceId)
					.quoted()
					.required()
				.stringField("trip_id")
					.setter(Trip::setTripId)
					.quoted()
					.required()
				.stringField("trip_headsign")
					.setter(Trip::setTripHeadsign)
					.quoted()
					.optional()
				.integerField("direction_id")
					.setter(Trip::setDirectionId)
					.quoted()
					.optional()
				.stringField("shape_id")
					.setter(Trip::setShapeId)
					.quoted()
					.optional()
				.integerField("wheelchair_accessible")
					.setter(Trip::setWheelchairAccessible)
					.quoted()
					.optional()
				.build();		
	}
	
	@Test
	public void compareToConventionalParserWithoutQuotes() throws Exception {

		CsvParser referenceParser = referenceParser(file, StandardCharsets.UTF_8);
		CsvReader<Trip> factory = parser(file, StandardCharsets.UTF_8);
		
		int count = 0;
		
		referenceParser.parseNext(); // first row
		do {
			Trip trip = factory.next();
			
			String[] row = referenceParser.parseNext();
			
			Trip referenceTrip;
			if(row != null) {
				referenceTrip = new Trip();
				referenceTrip.setRouteId(row[0]);
				referenceTrip.setTripId(row[1]);
				referenceTrip.setServiceId(row[2]);
				referenceTrip.setTripHeadsign(row[3]);
				if(row[4] != null && row[4].length() > 0) {
					referenceTrip.setDirectionId(Integer.parseInt(row[4]));
				}
				referenceTrip.setShapeId(row[5]);
				if(row[6] != null && row[6].length() > 0) {
					referenceTrip.setWheelchairAccessible(Integer.parseInt(row[6]));
				}
			} else {
				referenceTrip = null;
			}

			if(!Objects.equals(trip, referenceTrip)) {
				System.out.println("Line " + count);
				System.out.println(trip);
				System.out.println(referenceTrip);
			}
			assertThat(trip).isEqualTo(referenceTrip);

			if(trip == null) {
				break;
			}
			
			count++;
		} while(true);

		System.out.println("Parsed " + count + " lines");

	}
	
	@Test
	public void compareToConventionalParserWithQuotes() throws Exception {

		CsvParser referenceParser = referenceParser(quotedFile, StandardCharsets.ISO_8859_1);
		CsvReader<Trip> factory = quotedParser(quotedFile, StandardCharsets.ISO_8859_1);
		
		int count = 0;
		
		referenceParser.parseNext(); // first row
		do {
			Trip trip = factory.next();
			
			String[] row = referenceParser.parseNext();
			
			Trip referenceTrip;
			if(row != null) {
				referenceTrip = new Trip();
				referenceTrip.setRouteId(row[0]);
				referenceTrip.setTripId(row[1]);
				referenceTrip.setServiceId(row[2]);
				referenceTrip.setTripHeadsign(row[3]);
				if(row[4] != null && row[4].length() > 0) {
					referenceTrip.setDirectionId(Integer.parseInt(row[4]));
				}
				referenceTrip.setShapeId(row[5]);
				if(row[6] != null && row[6].length() > 0) {
					referenceTrip.setWheelchairAccessible(Integer.parseInt(row[6]));
				}
			} else {
				referenceTrip = null;
			}

			if(!Objects.equals(trip, referenceTrip)) {
				System.out.println("Line " + count);
				System.out.println(trip);
				System.out.println(referenceTrip);
			}
			assertThat(trip).isEqualTo(referenceTrip);

			if(trip == null) {
				break;
			}
			
			count++;
		} while(true);

		System.out.println("Parsed " + count + " lines");
	}
	
	public CsvReader<Trip> parser(File file, Charset charste) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charste);
		return plain.create(reader1);
	}
	
	public CsvReader<Trip> quotedParser(File file, Charset charset) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		return quoted.create(reader1);
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
