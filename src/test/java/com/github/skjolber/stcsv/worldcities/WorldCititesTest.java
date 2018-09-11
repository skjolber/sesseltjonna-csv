package com.github.skjolber.stcsv.worldcities;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.CsvMapper;

public class WorldCititesTest {

	private CsvMapper<City> consumer;
	private CsvMapper<City> consumerWithQuotes;

	private CsvMapper<City> setter;
	private CsvMapper<City> setterWithQuotes;

	@BeforeEach
	public void init() throws Exception {
		consumer = CsvMapper.builder(City.class)
				.stringField("Country")
					.consumer(City::setCountry)
					.required()
				.stringField("City")
					.consumer(City::setCity)
					.required()
				.stringField("AccentCity")
					.consumer(City::setAccentCity)
					.required()
				.stringField("Region")
					.consumer(City::setRegion)
					.quoted()
					.optional()
				.longField("Population")
					.consumer(City::setPopulation)
					.optional()
				.doubleField("Latitude")
					.consumer(City::setLatitude)
					.optional()
				.doubleField("Longitude")
					.consumer(City::setLongitude)
					.optional()
				.build();
		
		consumerWithQuotes = CsvMapper.builder(City.class)
				.stringField("Country")
					.consumer(City::setCountry)
					.quoted()
					.required()
				.stringField("City")
					.consumer(City::setCity)
					.quoted()
					.required()
				.stringField("AccentCity")
					.consumer(City::setAccentCity)
					.quoted()
					.optional()
				.stringField("Region")
					.consumer(City::setRegion)
					.quoted()
					.optional()
				.longField("Population")
					.consumer(City::setPopulation)
					.quoted()
					.optional()
				.doubleField("Latitude")
					.consumer(City::setLatitude)
					.quoted()
					.optional()
				.doubleField("Longitude")
					.consumer(City::setLongitude)
					.quoted()
					.optional()
				.build();		
		
		setter = CsvMapper.builder(City.class)
				.stringField("Country")
					.required()
				.stringField("City")
					.required()
				.stringField("AccentCity")
					.required()
				.stringField("Region")
					.quoted()
					.optional()
				.longField("Population")
					.optional()
				.doubleField("Latitude")
					.optional()
				.doubleField("Longitude")
					.optional()
				.build();
		
		// "Country","City","AccentCity","Region","Population","Latitude","Longitude"

		setterWithQuotes = CsvMapper.builder(City.class)
				.stringField("Country")
					.quoted()
					.required()
				.stringField("City")
					.quoted()
					.required()
				.stringField("AccentCity")
					.quoted()
					.optional()
				.stringField("Region")
					.quoted()
					.optional()
				.longField("Population")
					.quoted()
					.optional()
				.doubleField("Latitude")
					.quoted()
					.optional()
				.doubleField("Longitude")
					.quoted()
					.optional()
				.build();			
		
	}

	private void parseUntillNull(CsvReader<City> factory) throws Exception {
		List<City> trips = new ArrayList<>();
		int count = 0;
		try {
			do {
				City trip = factory.next();
				if(trip == null) {
					break;
				}

				assertNoEmptyStringValues(trip);
				
				trips.add(trip);
				count++;
			} while(true);
	
			if(count == 0) {
				throw new RuntimeException();
			}
		} catch(Exception e) {
			System.out.println("Problem at line " + count);
			
			throw new RuntimeException(e);
		}

	}
	
	// consumer

	private void assertNoEmptyStringValues(City city) {
		
		// Country,City,AccentCity,Region,Population,Latitude,Longitude

		assertNotEmptyString(city.getCountry());
		assertNotEmptyString(city.getCity());
		assertNotEmptyString(city.getAccentCity());
		assertNotEmptyString(city.getRegion());
	}

	private void assertNotEmptyString(String value) {
		assert value == null || !value.isEmpty();
	}

	@Test
	public void parseConsumerHeaderOnly() throws Exception {
		CsvReader<City> factory = parserWithConsumer(new File("src/test/resources/worldcities/worldcitiespop-1.txt"), StandardCharsets.UTF_8);
		assertNull(factory.next());
	}

	@Test
	public void parseConsumerHeaderOnlyWithQuotes() throws Exception {
		CsvReader<City> factory = parserWithConsumer(new File("src/test/resources/worldcities/worldcitiespop2-1.txt"), StandardCharsets.UTF_8);
		assertNull(factory.next());
	}
	
	@Test
	public void parseConsumer10Cities() throws Exception {
		CsvReader<City> factory = parserWithConsumer(new File("src/test/resources/worldcities/worldcitiespop-10.txt"), StandardCharsets.UTF_8);
		assertNotNull(factory.next());
		
		parseUntillNull(factory);
	}
	
	
	@Test
	public void parseConsumer10CitiesWithQuotes() throws Exception {
		CsvReader<City> factory = parserWithConsumerQuotes(new File("src/test/resources/worldcities/worldcitiespop2-10.txt"), StandardCharsets.UTF_8);
		assertNotNull(factory.next());
		
		parseUntillNull(factory);
	}
	
	@Test
	public void parseSetterHeaderOnly() throws Exception {
		CsvReader<City> factory = parserWithSetter(new File("src/test/resources/worldcities/worldcitiespop-1.txt"), StandardCharsets.UTF_8);
		assertNull(factory.next());
	}

	@Test
	public void parseSetterHeaderOnlyWithQuotes() throws Exception {
		CsvReader<City> factory = parserWithSetter(new File("src/test/resources/worldcities/worldcitiespop2-1.txt"), StandardCharsets.UTF_8);
		assertNull(factory.next());
	}
	
	@Test
	public void parseSetter250000OnlyWithQuotes() throws Exception {
		CsvReader<City> factory = parserWithSetterQuotes(new File("src/test/resources/worldcities/worldCities-withQuotes-250000.txt"), StandardCharsets.UTF_8);
		assertNotNull(factory.next());
		
		int count = 0;
		try {
			while(factory.next() != null) {
				count++;
			}
		} catch(Exception e) {
			System.out.println(count);
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void parseSetterCities() throws Exception {
		CsvReader<City> factory = parserWithSetter(new File("src/test/resources/worldcities/worldcitiespop-10.txt"), StandardCharsets.UTF_8);
		assertNotNull(factory.next());
		
		parseUntillNull(factory);
	}

	@Test
	public void parseSetter10CitiesWithQuotes() throws Exception {
		CsvReader<City> factory = parserWithSetterQuotes(new File("src/test/resources/worldcities/worldcitiespop2-10.txt"), StandardCharsets.UTF_8);
		assertNotNull(factory.next());
		
		parseUntillNull(factory);
	}
	
	
	public CsvReader<City> parserWithConsumer(File file, Charset charset) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		return consumer.create(reader1);
	}

	public CsvReader<City> parserWithConsumerQuotes(File file, Charset charset) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		return consumerWithQuotes.create(reader1);
	}

	public CsvReader<City> parserWithSetter(File file, Charset charset) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		return setter.create(reader1);
	}

	public CsvReader<City> parserWithSetterQuotes(File file, Charset charset) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		return setterWithQuotes.create(reader1);
	}
	
}
