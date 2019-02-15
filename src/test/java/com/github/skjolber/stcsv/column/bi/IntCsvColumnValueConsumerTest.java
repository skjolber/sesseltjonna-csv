package com.github.skjolber.stcsv.column.bi;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

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
        
        for(int i = 0; i < 16; i++) {
        	int number = Integer.MAX_VALUE >> i;
        	String text = Integer.toString(number);
        	assertEquals(number, parseInt(text.toCharArray(), 0, text.length()));
        }
        for(int i = 0; i < 16; i++) {
        	int number = Integer.MIN_VALUE >> i;
        	String text = Integer.toString(number);
        	assertEquals(number, parseInt(text.toCharArray(), 0, text.length()));
        }
    }
	
	private int parseInt(char[] ch, int i, int j) {
		return IntCsvColumnValueConsumer.parseInt(ch, i, i + j);
	}
  	
}
