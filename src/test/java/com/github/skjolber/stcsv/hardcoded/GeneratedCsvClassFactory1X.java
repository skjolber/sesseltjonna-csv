package com.github.skjolber.stcsv.hardcoded;

import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.CsvReaderStaticInitializer;
import com.github.skjolber.stcsv.CsvReaderStaticInitializer.CsvStaticFields;
import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.DoubleCsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.IntCsvColumnValueConsumer;
import com.github.skjolber.stcsv.hardcoded.StopTime;
import java.io.IOException;
import java.io.Reader;

public final class GeneratedCsvClassFactory1X extends AbstractCsvReader {
	private static final CsvColumnValueConsumer<StopTime> v2 = (object, array, start, end) -> object.setArrivalTime(StopTimeFieldMappingFactory.getStringAsSeconds(new String(array, start, end - start)));
	private static final CsvColumnValueConsumer<StopTime> v3 = (object, array, start, end) -> object.setArrivalTime(StopTimeFieldMappingFactory.getStringAsSeconds(new String(array, start, end - start)));

	public GeneratedCsvClassFactory1X(Reader reader) {
		super(reader, 21000000);
	}

	public GeneratedCsvClassFactory1X(Reader reader, char[] current, int offset, int length) {
		super(reader, current, offset, length);
	}

	public StopTime next() throws IOException {
		
		System.out.println("ABCD");
		
		return null;
	}
}