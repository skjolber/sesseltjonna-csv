package com.github.skjolber.stcsv.gtfs;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvMapper2;
import com.github.skjolber.stcsv.CsvReader;
/**
 * 
 * This unit test demonstrates the use of a intermediate processor within the constumer lambda function.
 *
 */
public class TripsWithIntermediateProcessorTest {

	private File file = new File("src/test/resources/gtfs/trips-plain-5000.txt");

	private CsvMapper2<Trip, Cache> mapper;

	public static class Cache {
		private Set<String> routes = new HashSet<>();
		
		public void add(String str) {
			routes.add(str);
		}
		
		public Set<String> getRoutes() {
			return routes;
		}
	}
	
	@BeforeEach
	public void init() throws Exception {
		
		mapper = CsvMapper2.builder(Trip.class, Cache.class)
				.stringField("route_id")
					.consumer((trip, cache, routeId) -> {
						cache.add(routeId);
						trip.setRouteId(routeId);
					})
					.setter(Trip::setRouteId)
					.quoted()
					.optional()
				.stringField("service_id")
					// just make sure the cache can be used by every field, not just the first
					.consumer((trip, cache, serviceId) -> {
						trip.setServiceId(serviceId);
					})
					.required()
				.stringField("trip_id")
					.setter(Trip::setTripId)
					.required()
				.stringField("trip_headsign")
					.setter(Trip::setTripHeadsign)
					.quoted()
					.optional()
				.stringField("shape_id")
					.setter(Trip::setShapeId)
					.optional()
				.build();
	}

	@Test
	public void parseWithCache() throws Exception {

		Cache cache = new Cache();
		CsvReader<Trip> factory = parser(file, StandardCharsets.UTF_8, cache);
		
		Set<String> routes = new HashSet<>();
		
		do {
			Trip trip = factory.next();
			
			if(trip == null) {
				break;
			}
		
			routes.add(trip.getRouteId());
		} while(true);
		
		assertThat(cache.getRoutes().size()).isEqualTo(routes.size());
	}

	public CsvReader<Trip> parser(File file, Charset charset, Cache cache) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		return mapper.create(reader1, cache); // note additional cache parameter
	}

	@Test
	public void testJustConsumers() throws Exception {
		CsvMapper2 parser = CsvMapper2.builder(Trip.class, Cache.class)
			.stringField("trip_id")
				.consumer( (t, id) -> t.setTripId(id) )
				.required()
			.stringField("shape_id")
				.consumer( (t, a, id) -> t.setShapeId(id) )
				.optional()
			.build();
		
		parser.buildDefaultStaticCsvMapper(true);
	}
}
