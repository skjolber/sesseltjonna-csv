package com.github.skjolber.stcsv.databinder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.skjolber.stcsv.databinder.column.bi.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.databinder.column.tri.CsvColumnValueTriConsumer;

public class CsvReaderStaticInitializer {

	public static class CsvStaticFields {
		private CsvColumnValueConsumer<?>[] biConsumers;
		private CsvColumnValueTriConsumer<?, ?>[] triConsumers;
		
		public CsvColumnValueConsumer<?>[] getBiConsumers() {
			return biConsumers;
		}
		
		public CsvColumnValueTriConsumer<?, ?>[] getTriConsumers() {
			return triConsumers;
		}

	}
	
	private CsvReaderStaticInitializer() {
	}
	
	/**
	 * {@linkplain ConcurrentHashMap} retrievals reflect the results of the most recently 
	 * completed update operations holding upon their onset, so the classloader will
	 * always be able to retrieve stored values.
	 */
	
	protected static final Map<String, CsvStaticFields> values = new ConcurrentHashMap<>();
	
	public static CsvStaticFields remove(String className) {
		return values.remove(className);
	}
	
	public static void add(String className, CsvColumnValueConsumer<?>[] biConsumers, CsvColumnValueTriConsumer<?, ?>[] triConsumers) {
		CsvStaticFields wrapper = new CsvStaticFields();
		wrapper.biConsumers = biConsumers;
		wrapper.triConsumers = triConsumers;
		
		values.put(className, wrapper);
	}

}
