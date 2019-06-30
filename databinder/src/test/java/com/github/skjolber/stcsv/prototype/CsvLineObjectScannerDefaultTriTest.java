package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.CsvMapper2;
import com.github.skjolber.stcsv.CsvReader;

public class CsvLineObjectScannerDefaultTriTest {

	private CsvMapper2<CsvLineObject, Cache> consumerMapping;
	
	private Cache cache = new Cache();
	
	public static class Cache {
		private Set<Object> values = new HashSet<>();
		
		public void add(Object str) {
			values.add(str);
		}
		
		public Set<Object> getValues() {
			return values;
		}
	}
	
	@BeforeEach
	public void init() throws Exception {
		consumerMapping = CsvMapper2.builder(CsvLineObject.class, Cache.class)
				.stringField("a")
					.consumer((value, cache, input) -> {
						cache.add(input);
						value.setStringValue(input);
					})
					.optional()
				.longField("b")
					.consumer((value, cache, input) -> {
						cache.add(input);
						value.setLongValue(input);
					})
					.required()
				.integerField("c")
					.consumer((value, cache, input) -> {
						cache.add(input);
						value.setIntegerValue(input);
					})
					.optional()
				.booleanField("f")
					.consumer((value, cache, input) -> {
						cache.add(input);
						value.setBooleanValue(input);
					})
					.optional()
				.doubleField("h")
					.consumer((value, cache, input) -> {
						cache.add(input);
						value.setDoubleValue(input);
					})
					.optional()
				.build();
			}
	
	@Test
	public void testConsumer() throws Exception {
		StringBuffer builder = new StringBuffer("a,b,c,d,e,f,g,h,i\n");

		String stringValue = "string";
		Long longValue = 1L;
		Integer integerValue = 2;
		Short shortValue = 3;
		Byte byteValue = 4;
		Boolean booleanValue = true;
		Character characterValue = 'a';
		Double doubleValue = 2.5;
		Float floatValue = 7.5f;
		
		builder.append(stringValue);
		builder.append(",");
		builder.append(longValue.toString());
		builder.append(",");
		builder.append(integerValue.toString());
		builder.append(",");
		builder.append(shortValue.toString());
		builder.append(",");
		builder.append(byteValue.toString());
		builder.append(",");
		builder.append(booleanValue.toString());
		builder.append(",");
		builder.append(characterValue.toString());
		builder.append(",");
		builder.append(doubleValue.toString());
		builder.append(",");
		builder.append(floatValue.toString());
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = consumerMapping.create(new StringReader(builder.toString()), cache);
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		assertThat(next.getLongValue()).isEqualTo(longValue);
		assertThat(next.getIntegerValue()).isEqualTo(integerValue);
		assertThat(next.getBooleanValue()).isEqualTo(booleanValue);
		assertThat(next.getDoubleValue()).isEqualTo(doubleValue);
		
		assertThat(scanner.next()).isNull();

	}


}
