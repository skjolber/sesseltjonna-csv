package com.github.skjolber.stcsv;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.builder.CsvMappingBuilder;

/**
 * 
 * Dynamic CSV parser generator. Adapts the underlying implementation according 
 * to the first (header) line.
 * <br><br>
 * Uses ASM to build the parsers.
 * <br><br>
 * Thread-safe.
 */

public class CsvMapper<T> extends AbstractCsvMapper<T> {

	protected final Map<String, StaticCsvMapper<T>> factories = new ConcurrentHashMap<>();

	public static <T> CsvMappingBuilder<T> builder(Class<T> cls) {
		return new CsvMappingBuilder<T>(cls);
	}

	public CsvMapper(Class<T> cls, char divider, List<AbstractColumn> columns, boolean skipEmptyLines,
			boolean skipComments, boolean skippableFieldsWithoutLinebreaks, ClassLoader classLoader, int bufferLength) {
		super(cls, divider, columns, skipEmptyLines, skipComments, skippableFieldsWithoutLinebreaks, classLoader, bufferLength);
	}

	public CsvReader<T> create(Reader reader) throws Exception {
		// avoid multiple calls to read when locating the first line
		// so read a full buffer
		char[] current = new char[bufferLength + 1];

		int start = 0;
		int end = 0;
		do {
			int read = reader.read(current, start, bufferLength - start);
			if(read == -1) {
				return new EmptyCsvReader<>();
			} else {
				end += read;
			}

			for(int i = start; i < end; i++) {
				if(current[i] == '\n') {
					return create(reader, new String(current, 0, i), current, i + 1, end);
				}
			}
			start += end;
		} while(end < bufferLength);

		throw new IllegalArgumentException("No linebreak found in " + current.length + " characters");
	}

	public CsvReader<T> create(Reader reader, String header, char[] current, int offset, int length) throws Exception {
		StaticCsvMapper<T> constructor = factories.get(header); // note: using the stringbuilder as a key does not work
		if(constructor == null) {
			boolean carriageReturns = header.length() > 1 && header.charAt(header.length() - 1) == '\r';
			List<String> fields = parseColumnNames(header);

			constructor = buildStaticCsvMapper(carriageReturns, fields);
			if(constructor == null) {
				return new EmptyCsvReader<>();
			}
			factories.put(header, constructor);
		}
		return constructor.newInstance(reader, current, offset, length);

	}
	
	// TODO builder pattern

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StaticCsvMapper<T> buildDefaultStaticCsvMapper(boolean carriageReturns) throws Exception {
		return new DefaultStaticCsvMapper(super.createDefaultReaderClass(carriageReturns));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StaticCsvMapper<T> buildStaticCsvMapper(String firstLine) throws Exception {
		boolean carriageReturns = firstLine.charAt(firstLine.length() - 2) == '\r';
		String line;
		if(carriageReturns) {
			line = firstLine.substring(0, firstLine.length() - 2);
		} else {
			line = firstLine.substring(0, firstLine.length() - 1);
		}
		return new DefaultStaticCsvMapper(super.createReaderClass(carriageReturns, line));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StaticCsvMapper<T> buildStaticCsvMapper(boolean carriageReturns, String header) throws Exception {
		return new DefaultStaticCsvMapper(super.createReaderClass(carriageReturns, header));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StaticCsvMapper<T> buildStaticCsvMapper(boolean carriageReturns, List<String> csvFileFieldNames) throws Exception {
		return new DefaultStaticCsvMapper(super.createReaderClass(carriageReturns, csvFileFieldNames));
	}

	@Override
	protected void writeTriConsumerVariable(String subClassInternalName, MethodVisitor mv) {
		throw new RuntimeException();
	}

}
