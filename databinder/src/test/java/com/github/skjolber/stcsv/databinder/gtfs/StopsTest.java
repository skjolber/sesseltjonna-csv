package com.github.skjolber.stcsv.databinder.gtfs;

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
import com.github.skjolber.stcsv.databinder.CsvMapper;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
/**
 * 
 * https://developers.google.com/transit/gtfs/reference/#stopstxt
 *
 */
public class StopsTest {

	private File file = new File("src/test/resources/gtfs/stops.txt");

	private CsvMapper<Stop> plain;
	
	@BeforeEach
	public void init() throws Exception {
		plain = CsvMapper.builder(Stop.class)
				.stringField("stop_id")
					.setter(Stop::setId)
					.required()
				.stringField("stop_code")
					.setter(Stop::setCode)
					.optional()
				.stringField("stop_name")
					.setter(Stop::setName)
					.quoted()
					.required()
				.stringField("stop_desc")
					.setter(Stop::setDesc)
					.quoted()
					.optional()
				.doubleField("stop_lat")
					.setter(Stop::setLat)
					.required()
				.doubleField("stop_lon")
					.consumer( (s, v) -> s.setLon(v))
					.required()
				.stringField("zone_id")
					.setter(Stop::setZoneId)
					.optional()
				.stringField("stop_url")
					.setter(Stop::setUrl)
					.optional()
				.integerField("location_type")
					.setter(Stop::setLocationType)
					.optional()
				.integerField("wheelchair_boarding")
					.setter(Stop::setWheelchairBoarding)
					.optional()
				.stringField("stop_timezone")
					.setter(Stop::setTimezone)
					.optional()
				.stringField("direction")
					.setter(Stop::setDirection)
					.optional()
				.stringField("platform_code")
					.setter(Stop::setPlatformCode)
					.optional()
				.integerField("vehicle_type")
					.setter(Stop::setVehicleType)
					.optional()
				.build();
	}
	
	@Test
	public void compareToConventionalParserWithoutQuotes() throws Exception {

		CsvParser referenceParser = referenceParser(file, StandardCharsets.UTF_8);
		CsvReader<Stop> factory = parser(file, StandardCharsets.UTF_8);
		
		int count = 0;
		
		referenceParser.parseNext(); // first row
		do {
			Stop trip = factory.next();
			
			String[] row = referenceParser.parseNext();
			
			// stop_id,stop_name,stop_lat,stop_lon,stop_desc,location_type,parent_station,wheelchair_boarding,vehicle_type,platform_code
			Stop referenceTrip;
			if(row != null) {
				referenceTrip = new Stop();
				referenceTrip.setId(row[0]);
				referenceTrip.setName(row[1]);
				referenceTrip.setLat(Double.parseDouble(row[2]));
				referenceTrip.setLon(Double.parseDouble(row[3]));
				referenceTrip.setDesc(row[4]);
				if(row[4] != null) {
					referenceTrip.setLocationType(Integer.parseInt(row[5]));
				}
				if(row[6] != null) {
					referenceTrip.setWheelchairBoarding(Integer.parseInt(row[7]));
				}
				referenceTrip.setVehicleType(Integer.parseInt(row[8]));
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
	
	public CsvReader<Stop> parser(File file, Charset charste) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charste);
		return plain.create(reader1);
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
