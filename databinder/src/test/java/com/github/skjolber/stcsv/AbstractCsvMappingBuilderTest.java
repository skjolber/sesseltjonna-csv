package com.github.skjolber.stcsv;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.prototype.CsvLineObject;

public class AbstractCsvMappingBuilderTest {
	
	private ClassLoader classloader = AbstractCsvMappingBuilderTest.class.getClassLoader();;
	
	private CsvMapper<CsvLineObject> consumerMapping;

	@BeforeEach
	public void init() throws Exception {
		consumerMapping = CsvMapper.builder(CsvLineObject.class)
				.classLoader(classloader)
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.optional()
				.build();
	}
		
	@Test
	public void testCustomClassLoader() {
		assertThat(consumerMapping.getClassLoader()).isSameAs(classloader);
	}
}
