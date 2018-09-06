package com.github.skjolber.stcsv.prototype;

import java.io.IOException;
import java.io.Reader;
import java.util.function.BiConsumer;

import com.github.skjolber.stcsv.AbstractCsvClassFactory;
import com.github.skjolber.stcsv.CsvStaticInitializer;
import com.github.skjolber.stcsv.CsvStaticInitializer.CsvStaticFields;
import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.StringCsvColumnValueConsumer;

public class CsvLineObjectScannerDefaultFinalInitializer extends AbstractCsvClassFactory<CsvLineObject> {

	static {
		BiConsumer<CsvLineObject, String> value = CsvLineObject::setStringValue;
		StringCsvColumnValueConsumer parser = new StringCsvColumnValueConsumer(value);
		
		CsvStaticInitializer.add(CsvLineObjectScannerDefaultFinalInitializer.class.getName(), new CsvColumnValueConsumer[] {parser});
	}
	
	public CsvLineObjectScannerDefaultFinalInitializer(Reader reader) {
		super(reader, 1024);
	}

	static {
		CsvStaticFields fields = CsvStaticInitializer.remove(CsvLineObjectScannerDefaultFinalInitializer.class.getName());
		
		CsvColumnValueConsumer[] biConsumerList = fields.getConsumers();
		
		stringValue0 = (StringCsvColumnValueConsumer) biConsumerList[0];
	}
	
	private final static StringCsvColumnValueConsumer stringValue0;
	
	@Override
	public CsvLineObject next() throws IOException {
		throw new RuntimeException();
	}

	public static CsvColumnValueConsumer getStringValue0() {
		return stringValue0;
	}
}
