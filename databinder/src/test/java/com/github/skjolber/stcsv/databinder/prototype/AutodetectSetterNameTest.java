package com.github.skjolber.stcsv.databinder.prototype;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.databinder.CsvMapper;

public class AutodetectSetterNameTest {

	private CsvMapper<CsvLineObject> reflectionSetterMapping;
	
	@Test
	public void throwsExceptionOnUnknownFieldName() throws Exception {
		assertThrows(CsvException.class, ()->{
			CsvMapper.builder(CsvLineObject.class)
				.stringField("unknownField")
					.optional()
				.build();	
	            } );
	}
}
