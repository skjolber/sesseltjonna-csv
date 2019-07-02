package com.github.skjolber.stcsv;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvException;

public class CsvExceptionTest {

	@Test
	public void testConstructors() throws Exception {
		assertThrows(CsvException.class, ()->{
        	throw new CsvException();
        } );
		assertThrows(CsvException.class, ()->{
        	throw new CsvException("Message");
        } );
		assertThrows(CsvException.class, ()->{
        	throw new CsvException(new IllegalArgumentException());
        } );
		assertThrows(CsvException.class, ()->{
        	throw new CsvException("Message", new IllegalArgumentException());
        } );
	}

}
