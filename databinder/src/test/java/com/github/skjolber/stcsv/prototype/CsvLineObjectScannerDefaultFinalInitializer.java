package com.github.skjolber.stcsv.prototype;

import java.io.IOException;
import java.io.Reader;
import java.util.function.BiConsumer;

import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.CsvReaderStaticInitializer;
import com.github.skjolber.stcsv.CsvReaderStaticInitializer.CsvStaticFields;
import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.column.bi.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.bi.StringCsvColumnValueConsumer;

public class CsvLineObjectScannerDefaultFinalInitializer extends AbstractCsvReader<CsvLineObject> {

	static {
		BiConsumer<CsvLineObject, String> value = CsvLineObject::setStringValue;
		StringCsvColumnValueConsumer<?> parser = new StringCsvColumnValueConsumer<>(value);
		
		CsvReaderStaticInitializer.add(CsvLineObjectScannerDefaultFinalInitializer.class.getName(), new CsvColumnValueConsumer[] {parser}, null);
	}
	
	public CsvLineObjectScannerDefaultFinalInitializer(Reader reader) {
		super(reader, 1024);
	}

	static {
		CsvStaticFields fields = CsvReaderStaticInitializer.remove(CsvLineObjectScannerDefaultFinalInitializer.class.getName());
		
		CsvColumnValueConsumer<?>[] biConsumerList = fields.getBiConsumers();
		
		stringValue0 = (StringCsvColumnValueConsumer<?>) biConsumerList[0];
	}
	
	private final static StringCsvColumnValueConsumer<?> stringValue0;
	
	@Override
	public CsvLineObject next() throws IOException {
		throw new CsvBuilderException();
	}

	public static CsvColumnValueConsumer getStringValue0() {
		return stringValue0;
	}
}
