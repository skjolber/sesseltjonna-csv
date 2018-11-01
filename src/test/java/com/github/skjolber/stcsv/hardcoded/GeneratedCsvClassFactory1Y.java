package com.github.skjolber.stcsv.hardcoded;

import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.IgnoredColumn;
import com.github.skjolber.stcsv.prototype.CsvLineObject;
import java.io.IOException;
import java.io.Reader;

public final class GeneratedCsvClassFactory1Y extends AbstractCsvReader {
	public GeneratedCsvClassFactory1Y(Reader reader) {
		super(reader, 65536);
	}

	public GeneratedCsvClassFactory1Y(Reader reader, char[] current, int offset, int length) {
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
			if (current[currentOffset] == '\n') {
				int value = this.currentRange;

				do {
					if (currentOffset == value) {
						if ((value = this.fill()) == 0) {
							return null;
						}

						currentOffset = 0;
					} else {
						++currentOffset;
					}
				} while (current[currentOffset] == '\n');
			}

			CsvLineObject value = new CsvLineObject();
			int start;
			if (current[currentOffset] != '"') {
				if (current[currentOffset] == ',') {
					throw new CsvException("Illegal value in field 'stringValue'");
				}

				start = currentOffset;

				do {
					++currentOffset;
				} while (current[currentOffset] != ',');

				value.setStringValue(new String(current, start, currentOffset - start));
			} else {
				System.out.println("ADFSADF");
				++currentOffset;
				start = currentOffset;

				while (true) {
					if (current[currentOffset] == 123) {
						System.arraycopy(current, start, current, start + 1, currentOffset - start);
						++currentOffset;
						++start;
					} else if (current[currentOffset] == 456) {
						if (currentOffset <= start) {
							throw new CsvException("Illegal value in field 'stringValue'");
						}
System.out.println("ASDddF");
						if (currentOffset - start != 0x0fffffff) {
							throw new CsvException("Illegal value in field 'a'");
						}
						System.out.println("ASDF");

						
						value.setStringValue(new String(current, start, currentOffset - start));

						do {
							++currentOffset;
						} while (current[currentOffset] != ',');
						
						break;
					}

					++currentOffset;
				}
				
				System.out.println("XYD");
			}

			++currentOffset;
			currentOffset = IgnoredColumn.skipToLineBreak(this, current, currentOffset);
			super.currentOffset = currentOffset;
			return value;
		} catch (ArrayIndexOutOfBoundsException var6) {
			throw new CsvException(var6);
		}
	}
}