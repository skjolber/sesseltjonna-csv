package com.github.skjolber.stcsv.builder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.column.bi.IntCsvColumnValueConsumer;
import com.github.skjolber.stcsv.prototype.CsvLineObject;

public class CsvMappingBuilderTest {

	@Test
	public void invalidBufferLength1() {
		CsvMappingBuilder builder = new CsvMappingBuilder(CsvMappingBuilder.class);
		builder.bufferLength(0);
		
		assertThrows(CsvBuilderException.class, ()->{
			builder.build();
	    } );
	}

	@Test
	public void invalidBufferLength2() {
		CsvMappingBuilder2 builder = new CsvMappingBuilder2(CsvMappingBuilder.class, Object.class);
		builder.bufferLength(0);
		
		assertThrows(CsvBuilderException.class, ()->{
			builder.build();
	    } );
	}

	@Test
	public void duplicateField() {
		assertThrows(CsvBuilderException.class, ()->{
			CsvMapper.builder(CsvLineObject.class)
					.stringField("stringValue2")
						.setter(CsvLineObject::setStringValue)
						.optional()
					.stringField("stringValue2")
						.setter(CsvLineObject::setStringValue)
						.optional()
					.build();	
	    } );
		
	}
	
	@Test
	public void buildsWithoutByteBuddy() {
		CsvMapper.builder(CsvLineObject.class)
				.withoutByteBuddy()
				.stringField("stringValue")
					.optional()
				.build();	
	}
	
}
