package com.github.skjolber.stcsv.prototype;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.CsvMapper;

public class CsvLineObjectScannerDefaultTest {

	private CsvMapper<CsvLineObject> consumerMapping;
	private CsvMapper<CsvLineObject> reflectionSetterMapping;
	private CsvMapper<CsvLineObject> proxySetterMapping;
	
	@BeforeEach
	public void init() throws Exception {
		consumerMapping = CsvMapper.builder(CsvLineObject.class)
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
				.build();
		
		reflectionSetterMapping = CsvMapper.builder(CsvLineObject.class)
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
		
		proxySetterMapping = CsvMapper.builder(CsvLineObject.class)
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

		CsvReader<CsvLineObject> scanner = consumerMapping.create(new StringReader(builder.toString()));
		
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

		CsvReader<CsvLineObject> scanner = reflectionSetterMapping.create(new StringReader(builder.toString()));
		
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

		CsvReader<CsvLineObject> scanner = proxySetterMapping.create(new StringReader(builder.toString()));
		
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

		CsvReader<CsvLineObject> scanner = reflectionSetterMapping.createDefaultScannerFactory(false).newInstance(new StringReader(builder.toString()));
		
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
