package com.github.skjolber.stcsv.builder;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class CsvBuilderExceptionTest {

	@Test
	public void testConstructors() throws Exception {
		assertThrows(CsvBuilderException.class, ()->{
        	throw new CsvBuilderException();
        } );
		assertThrows(CsvBuilderException.class, ()->{
        	throw new CsvBuilderException("Message");
        } );
		assertThrows(CsvBuilderException.class, ()->{
        	throw new CsvBuilderException(new IllegalArgumentException());
        } );
		assertThrows(CsvBuilderException.class, ()->{
        	throw new CsvBuilderException("Message", new IllegalArgumentException());
        } );
	}

}
