package com.github.skjolber.stcsv.databinder.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.databinder.CsvMapper;
import com.github.skjolber.stcsv.databinder.CsvMapper2;

public class CsvLineObjectScannerDefaultTriTest {

	// tri
	private CsvMapper2<CsvLineObject, Cache> triConsumerMapping;
	private Cache cache = new Cache();

	// all bi stuff should still work when using an intermediate 
	private CsvMapper2<CsvLineObject, Cache> consumerMapping;
	private CsvMapper2<CsvLineObject, Cache> reflectionSetterMapping;
	private CsvMapper2<CsvLineObject, Cache> proxySetterMapping;
	
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
		triConsumerMapping = CsvMapper2.builder(CsvLineObject.class, Cache.class)
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
				.field("i")
					.consumer((a, b, c, d, e) -> {
						a.setFloatValue(Float.parseFloat(new String(c, d, e - d)));
					})
					.optional()
					
				.build();
		
		consumerMapping = CsvMapper2.builder(CsvLineObject.class, Cache.class)
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.optional()
				.longField("b")
					.consumer(CsvLineObject::setLongValue)
					.required()
				.integerField("c")
					.consumer(CsvLineObject::setIntegerValue)
					.optional()
				.booleanField("f")
					.consumer(CsvLineObject::setBooleanValue)
					.optional()
				.doubleField("h")
					.consumer(CsvLineObject::setDoubleValue)
					.optional()
				.field("i")
					.consumer((a, b, c, d) -> {
						a.setFloatValue(Float.parseFloat(new String(b, c, d - c)));
					})
					.optional()
				.build();
		
		reflectionSetterMapping = CsvMapper2.builder(CsvLineObject.class, Cache.class)
				.stringField("stringValue")
					.optional()
				.longField("longValue")
					.optional()
				.integerField("integerValue")
					.optional()
				.booleanField("booleanValue")
					.optional()
				.doubleField("doubleValue")
					.optional()
				.build();		
		
		proxySetterMapping = CsvMapper2.builder(CsvLineObject.class, Cache.class)
				.stringField("stringValue2")
					.setter(CsvLineObject::setStringValue)
					.optional()
				.longField("longValue2")
					.setter(CsvLineObject::setLongValue)
					.optional()
				.integerField("integerValue2")
					.setter(CsvLineObject::setIntegerValue)
					.optional()
				.booleanField("booleanValue2")
					.setter(CsvLineObject::setBooleanValue)
					.optional()
				.doubleField("doubleValue2")
					.setter(CsvLineObject::setDoubleValue)
					.optional()
				.build();		
		
	}
	
	@Test
	public void testTriConsumer() throws Exception {
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

		CsvReader<CsvLineObject> scanner = triConsumerMapping.create(new StringReader(builder.toString()), cache);
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		assertThat(next.getLongValue()).isEqualTo(longValue);
		assertThat(next.getIntegerValue()).isEqualTo(integerValue);
		assertThat(next.getBooleanValue()).isEqualTo(booleanValue);
		assertThat(next.getDoubleValue()).isEqualTo(doubleValue);
		assertThat(next.getFloatValue()).isEqualTo(7.5f);

		assertThat(scanner.next()).isNull();

	}

	@Test
	public void testReflectionSetter() throws Exception {
		StringBuffer builder = new StringBuffer("stringValue,longValue,integerValue,d,e,booleanValue,g,doubleValue,i\n");

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

		CsvReader<CsvLineObject> scanner = reflectionSetterMapping.create(new StringReader(builder.toString()), cache);
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		assertThat(next.getLongValue()).isEqualTo(longValue);
		assertThat(next.getIntegerValue()).isEqualTo(integerValue);
		assertThat(next.getBooleanValue()).isEqualTo(booleanValue);
		assertThat(next.getDoubleValue()).isEqualTo(doubleValue);
		
		assertThat(scanner.next()).isNull();

	}

	@Test
	public void testProxySetterScanner() throws Exception {
		StringBuffer builder = new StringBuffer("stringValue2,longValue2,integerValue2,d,e,booleanValue2,g,doubleValue2,i\n");

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

		CsvReader<CsvLineObject> scanner = proxySetterMapping.create(new StringReader(builder.toString()), cache);
		
		CsvLineObject next = scanner.next();
		assertThat(next).isNotNull();
		
		assertThat(next.getStringValue()).isEqualTo(stringValue);
		assertThat(next.getLongValue()).isEqualTo(longValue);
		assertThat(next.getIntegerValue()).isEqualTo(integerValue);
		assertThat(next.getBooleanValue()).isEqualTo(booleanValue);
		assertThat(next.getDoubleValue()).isEqualTo(doubleValue);
		
		assertThat(scanner.next()).isNull();

	}

	@Test
	public void testDefault() throws Exception {
		StringBuffer builder = new StringBuffer(); // "stringValue,longValue,integerValue,booleanValue,doubleValue\n");

		String stringValue = "string";
		Long longValue = 1L;
		Integer integerValue = 2;
		Boolean booleanValue = true;
		Double doubleValue = 2.5;
		
		builder.append(stringValue);
		builder.append(",");
		builder.append(longValue.toString());
		builder.append(",");
		builder.append(integerValue.toString());
		builder.append(",");
		builder.append(booleanValue.toString());
		builder.append(",");
		builder.append(doubleValue.toString());
		builder.append("\n");

		CsvReader<CsvLineObject> scanner = reflectionSetterMapping.buildDefaultStaticCsvMapper(false).newInstance(new StringReader(builder.toString()), cache);
		
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
