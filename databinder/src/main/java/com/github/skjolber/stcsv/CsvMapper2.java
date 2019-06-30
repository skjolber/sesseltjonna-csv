package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.builder.CsvMappingBuilder2;

/**
 * 
 * Dynamic CSV parser generator. Adapts the underlying implementation according 
 * to the first (header) line.
 * <br><br>
 * Uses ASM to build the parser implementations.
 * <br><br>
 * Thread-safe.
 */

public class CsvMapper2<T, H> extends AbstractCsvMapper<T> {

	protected final Class<H> intermediate;
	protected final String intermediateInternalName;
	
	protected final Map<String, StaticCsvMapper2<T, H>> factories = new ConcurrentHashMap<>();

	public static <T, D> CsvMappingBuilder2<T, D> builder(Class<T> cls, Class<D> delegate) {
		return new CsvMappingBuilder2<T, D>(cls, delegate);
	}

	public CsvMapper2(Class<T> cls, Class<H> intermediate, char divider, List<AbstractColumn> columns, boolean skipEmptyLines,
			boolean skipComments, boolean skippableFieldsWithoutLinebreaks, ClassLoader classLoader, int bufferLength) {
		super(cls, divider, columns, skipEmptyLines, skipComments, skippableFieldsWithoutLinebreaks, classLoader, bufferLength);
		
		this.intermediate = intermediate;
		this.intermediateInternalName = getInternalName(intermediate);
	}

	public CsvReader<T> create(Reader reader, H helper) throws Exception {
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
					return create(reader, new String(current, 0, i), current, i + 1, end, helper);
				}
			}
			start += end;
		} while(end < bufferLength);

		throw new CsvException("No linebreak found in " + current.length + " characters");
	}

	public CsvReader<T> create(Reader reader, String header, char[] current, int offset, int length, H helper) throws Exception {
		StaticCsvMapper2<T, H> constructor = factories.get(header); // note: using the stringbuilder as a key does not work
		if(constructor == null) {
			boolean carriageReturns = header.length() > 1 && header.charAt(header.length() - 1) == '\r';
			List<String> fields = parseColumnNames(header);

			constructor = buildStaticCsvMapper(carriageReturns, fields);
			if(constructor == null) {
				return new EmptyCsvReader<>();
			}
			factories.put(header, constructor);
		}
		if(helper != null) {
			return constructor.newInstance(reader, current, offset, length, helper);
		}
		return constructor.newInstance(reader, current, offset, length, helper);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StaticCsvMapper2<T, H> buildDefaultStaticCsvMapper(boolean carriageReturns) throws Exception {
		return new DefaultStaticCsvMapper2(super.createDefaultReaderClass(carriageReturns), intermediate);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StaticCsvMapper2<T, H> buildStaticCsvMapper(String firstLine) throws Exception {
		boolean carriageReturns = firstLine.charAt(firstLine.length() - 2) == '\r';
		String line;
		if(carriageReturns) {
			line = firstLine.substring(0, firstLine.length() - 2);
		} else {
			line = firstLine.substring(0, firstLine.length() - 1);
		}
		return new DefaultStaticCsvMapper2(super.createReaderClass(carriageReturns, line), intermediate);
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StaticCsvMapper2<T, H> buildStaticCsvMapper(boolean carriageReturns, String header) throws Exception {
		return new DefaultStaticCsvMapper2(super.createReaderClass(carriageReturns, header), intermediate);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StaticCsvMapper2<T, H> buildStaticCsvMapper(boolean carriageReturns, List<String> csvFileFieldNames) throws Exception {
		return new DefaultStaticCsvMapper2(super.createReaderClass(carriageReturns, csvFileFieldNames), intermediate);
	}

	@Override
	protected void addConstructors(ClassWriter classWriter, String subClassInternalName) {
		super.addConstructors(classWriter, subClassInternalName, intermediateInternalName);
	}

	protected void writeTriConsumerVariable(String subClassInternalName, MethodVisitor mv) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, subClassInternalName, "intermediate", "L" + intermediateInternalName + ";");
		mv.visitVarInsn(ASTORE, intermediateIndex);
	}
}
