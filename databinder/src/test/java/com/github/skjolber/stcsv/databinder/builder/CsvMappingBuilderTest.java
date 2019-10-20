package com.github.skjolber.stcsv.databinder.builder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.databinder.CsvMapper;
import com.github.skjolber.stcsv.databinder.CsvMapper2;
import com.github.skjolber.stcsv.databinder.builder.CsvMappingBuilder;
import com.github.skjolber.stcsv.databinder.builder.CsvMappingBuilder2;
import com.github.skjolber.stcsv.databinder.column.bi.IntCsvColumnValueConsumer;
import com.github.skjolber.stcsv.databinder.prototype.CsvLineObject;

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

	@Test
	public void anonymousFieldRequiresConsumer() {
		assertThrows(CsvBuilderException.class, ()->{
			CsvMapper.builder(CsvLineObject.class)
			.field("anon")
				.optional()
			.build();
	    } );
		assertThrows(CsvBuilderException.class, ()->{
			CsvMapper2.builder(CsvLineObject.class, Object.class)
			.field("anon")
				.optional()
			.build();
	    } );
	}
}
