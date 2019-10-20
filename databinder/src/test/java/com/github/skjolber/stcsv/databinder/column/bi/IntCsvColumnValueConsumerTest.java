package com.github.skjolber.stcsv.databinder.column.bi;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.databinder.column.bi.IntCsvColumnValueConsumer;

public class IntCsvColumnValueConsumerTest {
	
	@Test
    public void testIntParsing() throws Exception {
        char[] testChars = "123456789".toCharArray();

        assertEquals(3, parseInt(testChars, 2, 1));
        assertEquals(123, parseInt(testChars, 0, 3));
        assertEquals(2345, parseInt(testChars, 1, 4));
        assertEquals(9, parseInt(testChars, 8, 1));
        assertEquals(456789, parseInt(testChars, 3, 6));
        assertEquals(23456, parseInt(testChars, 1, 5));
        assertEquals(123456789, parseInt(testChars, 0, 9));

        testChars = "32".toCharArray();
        assertEquals(32, parseInt(testChars, 0, 2));
        testChars = "189".toCharArray();
        assertEquals(189, parseInt(testChars, 0, 3));

        testChars = "10".toCharArray();
        assertEquals(10, parseInt(testChars, 0, 2));
        assertEquals(0, parseInt(testChars, 1, 1));
        
    	int number = Integer.MAX_VALUE;
        for(int i = 0; i < 10; i++) {
        	String p = Integer.toString(number);
        	assertEquals(number, parseInt(p.toCharArray(), 0, p.length()));
        	String n = Integer.toString(-number);
        	assertEquals(-number, parseInt(n.toCharArray(), 0, n.length()));
        	
        	number = number / 10;
        }
    }

	private int parseInt(char[] ch, int i, int j) {
		return IntCsvColumnValueConsumer.parseInt(ch, i, i + j);
	}
	
	@Test
	public void testTestInvalidThrowsException() {
		assertThrows(NumberFormatException.class, ()->{
			IntCsvColumnValueConsumer.parseInt("-".toCharArray(), 0, 1);
	    } );
		assertThrows(NumberFormatException.class, ()->{
			IntCsvColumnValueConsumer.parseInt("a".toCharArray(), 0, 1);
	    } );
		assertThrows(NumberFormatException.class, ()->{
			IntCsvColumnValueConsumer.parseInt("1a".toCharArray(), 0, 2);
	    } );
		assertThrows(NumberFormatException.class, ()->{
			IntCsvColumnValueConsumer.parseInt("11a".toCharArray(), 0, 3);
	    } );
		assertThrows(NumberFormatException.class, ()->{
			IntCsvColumnValueConsumer.parseInt("111a".toCharArray(), 0, 4);
	    } );
		assertThrows(NumberFormatException.class, ()->{
			IntCsvColumnValueConsumer.parseInt("1111a".toCharArray(), 0, 5);
	    } );
		assertThrows(NumberFormatException.class, ()->{
			IntCsvColumnValueConsumer.parseInt("01234567890123456789".toCharArray(), 0, 20);
	    } );
	}
}
