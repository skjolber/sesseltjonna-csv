package com.github.skjolber.stcsv.hardcoded;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.gtfs.Trip;

public class StopTimeBenchmark {

	private static File file = new File("/tmp/stoptimes.zip-plain-1000000.txt");		
	//private static File file = new File("src/test/resources/StopTimesNoHeader.txt");		

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		System.gc();
		
		CsvMapper<StopTime> mapping = CsvMapper.builder(StopTime.class)
				.bufferLength(123)
				.stringField("stop_id")
					.required()
				.stringField("trip_id")
					.required()
				.field("arrival_time")
				    .consumer((object, array, start, end) -> object.setArrivalTime(StopTimeFieldMappingFactory.getStringAsSeconds(new String(array, start, end - start))))
					.optional()
				.field("departure_time")
					.consumer((object, array, start, end) -> object.setDepartureTime(StopTimeFieldMappingFactory.getStringAsSeconds(new String(array, start, end - start))))
					.optional()
				.integerField("timepoint")
					.optional()
				.integerField("stop_sequence")
					.optional()
				.stringField("stop_headsign")
					.optional()
				.stringField("route_short_name")
					.optional()
				.integerField("pickup_type")
					.optional()
				.integerField("drop_off_type")
					.optional()
				.doubleField("shape_dist_traveled")
					.optional()
				.stringField("fare_period_id")
					.optional()
				.build();
			
		/*
		InputStream in = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(in, StandardCharsets.ISO_8859_1);

		String string = IOUtils.toString(reader);
		*/
		int n = 13;

		for(int i = 0; i < 100; i++) {
			//parseHardcoded(n, file);
			
			//parseHardcodedInline(n, file);
			//parseHardcodedInlineSetter(n, file);
			parseDynamic(mapping, n, file);
			
			/*
			parseDynamic(mapping, n, file);
			parseHardcodedInline(n, file);
			parseHardcodedInlineSetter(n, file);

			//parseDynamic(mapping, n, file);
			parseHardcoded(n, file);
			parseHardcodedInline(n, file);
			parseHardcodedInlineSetter(n, file);
			*/
		}		
	}

	private static int parseDynamic(CsvMapper<StopTime> mapping, int n, File file) throws FileNotFoundException, Exception, IOException {
		// https://www.beyondjava.net/quick-guide-writing-byte-code-asm
		
		
		for(int i = 0; i < n; i++) {
			long time = System.currentTimeMillis();
			
			InputStream in = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(in, StandardCharsets.ISO_8859_1);
			
			long before = System.nanoTime();
			CsvReader<StopTime> factory = mapping.create(reader);
			long after = System.nanoTime();

			System.out.println("Create in " + (after - before));
			
			long parseTime = System.currentTimeMillis();
			List<StopTime> StopTimes = new ArrayList<>();
			int count = 0;
			do {
				StopTime StopTime = factory.next();
				if(StopTime == null) {
					break;
				}
				StopTimes.add(StopTime);
				count++;
			} while(true);
	
			if(count == 0) {
				throw new RuntimeException();
			}
	
			System.out.println("Dynamic in " + (System.currentTimeMillis() - time) + " (" + (System.currentTimeMillis() - parseTime) + ") for " + StopTimes.size());
			
			System.gc();
		}
		return n;
	}

	private static int parseHardcodedInline(int n, File file)
			throws FileNotFoundException, Exception, IOException {
		// https://www.beyondjava.net/quick-guide-writing-byte-code-asm
		
		
		for(int i = 0; i < n; i++) {
			long time = System.currentTimeMillis();
			long parseTime = System.currentTimeMillis();
			
			InputStream in = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(in, StandardCharsets.ISO_8859_1);
			while(reader.read() != '\n');
			
			GeneratedCsvClassFactory1X factory = new GeneratedCsvClassFactory1X(reader);

			List<StopTime> trips = new ArrayList<>();
			int count = 0;
			do {
				StopTime trip = factory.next();
				if(trip == null) {
					break;
				}
				trips.add(trip);
				count++;
			} while(true);
	
			if(count == 0) {
				throw new RuntimeException();
			}
	
			System.out.println("Hardcoded inline in " + (System.currentTimeMillis() - time) + " (" + (System.currentTimeMillis() - parseTime) + ") for " + trips.size());
			
			System.gc();
		}
		return n;
	}
}
