package com.github.skjolber.stcsv;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.prototype.CsvLineObject;

public class CsvMapperTest {

	private CsvMapper<CsvLineObject> consumerMapping;
	
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
	}
	
	@Test
	public void testBuild1() throws Exception {
		consumerMapping.buildDefaultStaticCsvMapper(false);
		consumerMapping.buildStaticCsvMapper(false, "a,b,c,d,e,f,g,h");
		consumerMapping.buildStaticCsvMapper("a,b,c,d,e,f,g,h\n");
	}
	
	@Test
	public void testBuild2() throws Exception {
		consumerMapping.buildStaticCsvMapper(false, "a,b,c,d,e,f,g,h\r\n");
		consumerMapping.buildStaticCsvMapper("a,b,c,d,e,f,g,h\r\n");
	}	
}
