package com.github.skjolber.stcsv.databinder.column.bi;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.databinder.column.bi.BooleanCsvColumnValueConsumer;

public class BooleanCsvColumnValueConsumerTest {

	@Test
	public void parseRegulars() {
		assertTrue(BooleanCsvColumnValueConsumer.parseBoolean("true".toCharArray(), 0, 4));
		assertTrue(BooleanCsvColumnValueConsumer.parseBoolean("TRUE".toCharArray(), 0, 4));
		assertTrue(BooleanCsvColumnValueConsumer.parseBoolean("tRUE".toCharArray(), 0, 4));
		assertTrue(BooleanCsvColumnValueConsumer.parseBoolean("TrUE".toCharArray(), 0, 4));
		assertTrue(BooleanCsvColumnValueConsumer.parseBoolean("TRuE".toCharArray(), 0, 4));
		assertTrue(BooleanCsvColumnValueConsumer.parseBoolean("TRUe".toCharArray(), 0, 4));
	}
	
	@Test
	public void parseAlmost() {
		assertFalse(BooleanCsvColumnValueConsumer.parseBoolean("frue".toCharArray(), 0, 4));
		assertFalse(BooleanCsvColumnValueConsumer.parseBoolean("tlue".toCharArray(), 0, 4));
		assertFalse(BooleanCsvColumnValueConsumer.parseBoolean("troe".toCharArray(), 0, 4));
		assertFalse(BooleanCsvColumnValueConsumer.parseBoolean("trua".toCharArray(), 0, 4));
	}
	
}
