package com.github.skjolber.stcsv.gtfs;


import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.CsvReaderStaticInitializer;
import com.github.skjolber.stcsv.CsvReaderStaticInitializer.CsvStaticFields;
import com.github.skjolber.stcsv.IgnoredColumn;
import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.StringCsvColumnValueConsumer;
import com.github.skjolber.stcsv.prototype.CsvLineObject;
import java.io.IOException;
import java.io.Reader;

public final class GeneratedCsvClassFactory123 extends AbstractCsvReader {
	private static final StringCsvColumnValueConsumer v0;

	static {
		CsvStaticFields fields = CsvReaderStaticInitializer
				.remove("com.github.skjolber.stcsv.GeneratedCsvClassFactory1");
		CsvColumnValueConsumer[] consumerList = fields.getConsumers();
		v0 = (StringCsvColumnValueConsumer) consumerList[0];
	}

	public GeneratedCsvClassFactory123(Reader reader) {
		super(reader, 65536);
	}

	public GeneratedCsvClassFactory123(Reader reader, char[] current, int offset, int length) {
		super(reader, current, offset, length);
	}

	public Object next() throws IOException {
		int currentOffset = super.currentOffset;
		if (currentOffset >= super.currentRange) {
			if (this.fill() == 0) {
				return null;
			}

			currentOffset = 0;
		}

		char[] current = super.current;

		try {

			System.out.println("BEFORE");
			while (current[currentOffset] == '#' || current[currentOffset] == '\n' ) {
				int value = this.currentRange;

				while(true) {
					if(current[currentOffset] == '\n') {
						if (currentOffset == value) {
							if ((value = this.fill()) == 0) {
								return null;
							}
	
							currentOffset = 0;
						} else {
							currentOffset++;
						}
						break;
					}
					currentOffset++;
				}
			}
			
			System.out.println("AFTER");
			
			CsvLineObject value = new CsvLineObject();
			if (current[currentOffset] == ',') {
				throw new CsvException("Illegal value in field 'stringValue'");
			} else {
				int start = currentOffset;

				do {
					++currentOffset;
				} while (current[currentOffset] != ',');

				v0.consume(value, current, start, currentOffset);
				++currentOffset;
				currentOffset = IgnoredColumn.skipToLineBreak(this, current, currentOffset);
				super.currentOffset = currentOffset;
				return value;
			}
		} catch (ArrayIndexOutOfBoundsException var6) {
			throw new CsvException(var6);
		}
	}
}