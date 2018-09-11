package com.github.skjolber.stcsv;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;

public class CsvReaderStaticInitializer {

	public static class CsvStaticFields {
		private CsvColumnValueConsumer<?>[] consumers;
		
		public CsvColumnValueConsumer<?>[] getConsumers() {
			return consumers;
		}
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
	
	public static void add(String className, CsvColumnValueConsumer<?>[] consumers) {
		CsvStaticFields wrapper = new CsvStaticFields();
		wrapper.consumers = consumers;
		
		values.put(className, wrapper);
	}

}
