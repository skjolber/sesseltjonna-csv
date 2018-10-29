package com.github.skjolber.stcsv.hardcoded;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.gtfs.Trip;

public class Profiler {
	
	private File file = new File("src/test/resources/gtfs/trips.txt");		

	private CsvMapper<Trip> mapping;
	
	public Profiler() throws Exception {
		mapping = CsvMapper.builder(Trip.class)
				.stringField("route_id")
					.required()
				.stringField("service_id")
					.required()
				.stringField("trip_id")
					.required()
				.stringField("trip_headsign")
					.quoted()
					.optional()
				.integerField("direction_id")
					.optional()
				.stringField("shape_id")
					.optional()
				.integerField("wheelchair_accessible")
					.optional()
				.build();
	}
	
	public CsvReader<Trip> parser(File file, Charset charste) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader = new InputStreamReader(input, StandardCharsets.ISO_8859_1);
		
		return mapping.create(reader);
	}
	
	public int parse() throws Exception {
		CsvReader<Trip> factory = parser(file, StandardCharsets.ISO_8859_1);
		int count = 0;
		
		do {
			Trip trip = factory.next();

			if(trip == null) {
				break;
			}
			
			count++;
		} while(true);

		return count;
	}
	
	
	public static final void main(String[] args) throws Exception {
		Profiler profiler = new Profiler();
		
		int n = Integer.MAX_VALUE;
		for(int i = 0; i < n; i++) {
			if(profiler.parse() == 0) {
				throw new RuntimeException();
			}
		}
		
	}
}
