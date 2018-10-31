package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPLT;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SIPUSH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.skjolber.stcsv.builder.CsvMappingBuilder;
import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;

/**
 * 
 * Dynamic CSV parser generator. Adapts the underlying implementation according 
 * to the first (header) line.
 * <br><br>
 * Uses ASM to build the parsers.
 * <br><br>
 * Thread-safe.
 */

public class CsvMapper<T> {

	protected static final String GENERATED_CLASS_SIMPLE_NAME = "GeneratedCsvClassFactory%d";
	protected static final String GENERATED_CLASS_FULL_NAME = "com.github.skjolber.stcsv." + GENERATED_CLASS_SIMPLE_NAME;
	protected static final String GENERATED_CLASS_FULL_INTERNAL = "com/github/skjolber/stcsv/" + GENERATED_CLASS_SIMPLE_NAME;
	
	protected static final String superClassInternalName = getInternalName(AbstractCsvReader.class);
	protected static final String csvStaticInitializer = getInternalName(CsvReaderStaticInitializer.class);
	protected static final String ignoredColumnName = getInternalName(IgnoredColumn.class);
	protected static final String consumerName = getInternalName(CsvColumnValueConsumer.class);
	
	protected static AtomicInteger counter = new AtomicInteger();

	public static <T> CsvMappingBuilder<T> builder(Class<T> cls) {
		return new CsvMappingBuilder<T>(cls);
	}
	
	public static String getInternalName(Class<?> cls) {
		return getInternalName(cls.getName());
	}

	public static String getInternalName(String className) {
		return className.replace('.', '/');
	}

	protected int divider;
	private int quote;
	private int escapeCharacter;
	protected Class<T> mappedClass;
	
	protected String mappedClassInternalName;
	
	protected Map<String, AbstractColumn> keys = new HashMap<>(); // thread safe for reading
	protected List<AbstractColumn> columns;
	
	protected final boolean skipEmptyLines;
	protected final boolean skippableFieldsWithoutLinebreaks;
	protected final int bufferLength;
	
	/**
	 * Note: Stack variable types are fixed througout the application, as below. 
	 * 
	 * The range index is dual purpose as end index for trimming quoted content.
	 * 
	 */
	
	protected final int currentOffsetIndex = 1;
	protected final int currentArrayIndex = 2;
	protected final int objectIndex = 3;
	protected final int startIndex = 4;
	protected final int rangeIndex = 5;
	
	protected final Map<String, CsvReaderConstructor<T>> factories = new ConcurrentHashMap<>();
	protected ClassLoader classLoader;
	
	public CsvMapper(Class<T> cls, char divider, char quote, char escapeCharacter, List<AbstractColumn> columns, boolean skipEmptyLines, boolean skippableFieldsWithoutLinebreaks, ClassLoader classLoader, int bufferLength) {
		this.mappedClass = cls;
		this.divider = divider;
		this.quote = quote;
		this.escapeCharacter = escapeCharacter;
		this.columns = columns;

		this.skipEmptyLines = skipEmptyLines;
		this.skippableFieldsWithoutLinebreaks = skippableFieldsWithoutLinebreaks;
		this.classLoader = classLoader;
		this.bufferLength = bufferLength;
		
		for (AbstractColumn column : columns) {
			keys.put(column.getName(),  column);

			column.setParent(this);
			column.setVariableIndexes(currentArrayIndex, currentOffsetIndex, objectIndex, startIndex, rangeIndex);
		}
		
		mappedClassInternalName = getInternalName(mappedClass);
	}

	public Class<T> getMappedClass() {
		return mappedClass;
	}
	
	public int getDivider() {
		return divider;
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
		CsvReaderConstructor<T> constructor = factories.get(header); // note: using the stringbuilder as a key does not work
		if(constructor == null) {
			boolean carriageReturns = header.length() > 1 && header.charAt(header.length() - 1) == '\r';
			List<String> fields = parseNames(header);
			
			constructor = createScannerFactory(carriageReturns, fields);
			if(constructor == null) {
				return new EmptyCsvReader<>();
			}
		    factories.put(header, constructor);
		}
		return constructor.newInstance(reader, current, offset, length);
		
	}
	
	public CsvReaderConstructor<T> createDefaultScannerFactory(boolean carriageReturns) throws Exception {
		List<String> names = new ArrayList<>();
		for (AbstractColumn column: columns) {
			names.add(column.getName());
		}
		return createScannerFactory(carriageReturns, names);
	}

	public CsvReaderConstructor<T> createScannerFactory(boolean carriageReturns, String header) throws Exception {
		return createScannerFactory(carriageReturns, parseNames(header));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CsvReaderConstructor<T> createScannerFactory(boolean carriageReturns, List<String> csvFileFieldNames) throws Exception {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

		String subClassName = write(classWriter, csvFileFieldNames, carriageReturns);
		if(subClassName == null) {
			return null;
		}
		CsvReaderClassLoader<AbstractCsvReader<T>> loader = new CsvReaderClassLoader<AbstractCsvReader<T>>(classLoader);
		
		/*
		FileOutputStream fout = new FileOutputStream(new File("./my.class"));
		fout.write(classWriter.toByteArray());
		fout.close();
		*/
		
		Class<? extends AbstractCsvReader<T>> generatedClass = loader.load(classWriter.toByteArray(), subClassName);
		return new CsvReaderConstructor(generatedClass);
	}
	
	protected String write(ClassWriter classWriter, List<String> csvFileFieldNames, boolean carriageReturns) {
		int subclassNumber = counter.incrementAndGet();
		String subClassName = String.format(GENERATED_CLASS_FULL_NAME, subclassNumber);
		String subClassInternalName = String.format(GENERATED_CLASS_FULL_INTERNAL, subclassNumber);

		AbstractColumn[] mapping = new AbstractColumn[csvFileFieldNames.size()]; 
		
		CsvColumnValueConsumer<?>[] consumers = new CsvColumnValueConsumer[mapping.length];
		
		boolean inline = true;
		boolean consumer = false;
		
		int lastIndex = -1;
		int firstIndex = -1;
		for (int j = 0; j < csvFileFieldNames.size(); j++) {
			String name = csvFileFieldNames.get(j);
			AbstractColumn field = keys.get(name);
			
			if(field != null) {
				mapping[j] = field;
				
				if(firstIndex == -1) {
					firstIndex = j;
				}
				
				consumers[j] = field.getConsumer();
				if(consumers[j] != null) {
					consumer = true;
				}
				
				lastIndex = j;
			}
		}
		
		if(lastIndex == -1) {
			return null;
		}
		
		// https://stackoverflow.com/questions/34589435/get-the-enclosing-class-of-a-java-lambda-expression
		CsvReaderStaticInitializer.add(subClassName, consumers);
		
		// generics does not work when generating multiple classes; 
		// fails for class number 2 because of failing method signature
		// TODO still generate such a beast for the first?
		classWriter.visit(Opcodes.V1_8,
				ACC_FINAL | ACC_PUBLIC,
		        subClassInternalName,
		        null,
		        superClassInternalName,
		        null);

		if(consumer) {
			// write fields
			fields(classWriter, mapping);
			
			// static initializer
			staticInitializer(classWriter, mapping, subClassInternalName, subClassName);
		}
		
		// constructor with reader
		constructor(classWriter, subClassInternalName);

		// parse main method
		{
			MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "next", "()Ljava/lang/Object;", null, new String[] { "java/io/IOException" });

			mv.visitCode();
			Label startVariableScope = new Label();
			mv.visitLabel(startVariableScope);
			
			// init offset and char array
			// int currentOffset = this.currentOffset;
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, superClassInternalName, "currentOffset", "I");
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
			
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, superClassInternalName, "currentRange", "I");
			Label l2 = new Label();
			mv.visitJumpInsn(IF_ICMPLT, l2);
			
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, superClassInternalName, "fill", "()I", false);
			Label l4 = new Label();
			mv.visitJumpInsn(IFNE, l4);
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(ARETURN);
			mv.visitLabel(l4);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
			mv.visitLabel(l2);

			// final char[] current = this.current;
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, superClassInternalName, "current", "[C");
			mv.visitVarInsn(ASTORE, currentArrayIndex);		    	
			
			
			// try-catch block
			Label startTryCatch = new Label();
			
			Label endVariableScope = new Label();

			//Label endTryCatch = new Label();
			Label exceptionHandling = new Label();
			mv.visitTryCatchBlock(startTryCatch, endVariableScope, exceptionHandling, "java/lang/ArrayIndexOutOfBoundsException");
			
			mv.visitLabel(startTryCatch);
			
			if(skipEmptyLines) {
				writeSkipEmptyLines(mv, subClassInternalName, carriageReturns);
			}
			
			// init value object, i.e. the object to which data-binding will occur
			mv.visitTypeInsn(NEW, getInternalName(mappedClass.getName()));
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, getInternalName(mappedClass.getName()), "<init>", "()V", false);
			mv.visitVarInsn(ASTORE, objectIndex);

			if(firstIndex > 0) {
				// skip first column(s)
				skipColumns(mv, firstIndex);
			}
			
			int current = firstIndex;
			do {
				AbstractColumn column = mapping[current];
				
				if(current == mapping.length - 1) {
					column.last(mv, subClassInternalName, carriageReturns, inline);
				} else {
					column.middle(mv, subClassInternalName, inline);
				}
				
				// at last
				if(current == lastIndex) {
					if(lastIndex + 1 < mapping.length) {
						// skip rest of line
						skipToLinebreak(mv);
					}
					break;
				} else {
					int previous = current;
					
					current++;
					
					while(mapping[current] == null) {
						current++;
					}
					
					if(current - previous > 1) {
						// skip middle column
						skipColumns(mv, current - previous - 1);
					}
				}
			} while(true);
			
			// save value
			saveCurrentOffset(mv, superClassInternalName, currentOffsetIndex);		    	
			
			// return object
			mv.visitVarInsn(ALOAD, objectIndex);
			mv.visitInsn(ARETURN);

			mv.visitLabel(endVariableScope);

			// throw block
			mv.visitLabel(exceptionHandling);
			mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/ArrayIndexOutOfBoundsException"});
			mv.visitVarInsn(ASTORE, 1);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(33, l3);
			mv.visitTypeInsn(NEW, "com/github/skjolber/stcsv/CsvException");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, "com/github/skjolber/stcsv/CsvException", "<init>", "(Ljava/lang/Throwable;)V", false);
			mv.visitInsn(ATHROW);

			
			
			
			mv.visitLocalVariable("this", "L" + subClassInternalName + ";", null, startVariableScope, endVariableScope, 0);
			mv.visitLocalVariable("value", "L" + mappedClassInternalName + ";", null, startVariableScope, endVariableScope, objectIndex);
			mv.visitLocalVariable("currentOffset", "I", null, startVariableScope, endVariableScope, currentOffsetIndex);
			mv.visitLocalVariable("current", "[C", null, startVariableScope, endVariableScope, currentArrayIndex);
			if(inline) {
				mv.visitLocalVariable("start", "I", null, startVariableScope, endVariableScope, startIndex);
				mv.visitLocalVariable("rangeIndex", "I", null, startVariableScope, endVariableScope, rangeIndex);
			}

			mv.visitMaxs(0, 0); // calculated by the asm library
			mv.visitEnd();
		}
		
		classWriter.visitEnd();
		return subClassName;
	}

	protected void skipToLinebreak(MethodVisitor mv) {
		if(skippableFieldsWithoutLinebreaks) {
			//skipToLineBreakWithoutLinebreak							
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitMethodInsn(INVOKESTATIC, ignoredColumnName, "skipToLineBreakWithoutLinebreak", "(L" + superClassInternalName + ";[CI)I", false);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
		} else {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitMethodInsn(INVOKESTATIC, ignoredColumnName, "skipToLineBreak", "(L" + superClassInternalName + ";[CI)I", false);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
		}
	}

	protected void skipColumns(MethodVisitor mv, int count) {
		if(skippableFieldsWithoutLinebreaks) {
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitIntInsn(BIPUSH, divider);
			mv.visitIntInsn(BIPUSH, count);
			mv.visitMethodInsn(INVOKESTATIC, ignoredColumnName, "skipColumnsWithoutLinebreak", "([CICI)I", false);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
		} else {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitIntInsn(BIPUSH, divider);
			mv.visitIntInsn(BIPUSH, count);
			mv.visitMethodInsn(INVOKESTATIC, ignoredColumnName, "skipColumns", "(L" + superClassInternalName + ";[CICI)I", false);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
		}
	}

	protected void writeSkipEmptyLines(MethodVisitor mv, String subClassInternalName, boolean carriageReturns) {
		if(!carriageReturns) {
			/**
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
				} while (current[currentOffset] != '\n');
			}					
			*/
			final int rangeVariableIndex = 3;
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 10); // \n
			Label l5 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l5);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, subClassInternalName, "currentRange", "I");
			mv.visitVarInsn(ISTORE, rangeVariableIndex);
			Label l7 = new Label();
			mv.visitLabel(l7);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, rangeVariableIndex);
			Label l8 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l8);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "fill", "()I", false);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ISTORE, rangeVariableIndex);
			Label l10 = new Label();
			mv.visitJumpInsn(IFNE, l10);
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(ARETURN);
			mv.visitLabel(l10);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
			Label l13 = new Label();
			mv.visitJumpInsn(GOTO, l13);
			mv.visitLabel(l8);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitLabel(l13);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 10); // \n
			mv.visitJumpInsn(IF_ICMPEQ, l7);
			mv.visitLabel(l5);
			
		} else {
			
			/**
			if (current[currentOffset] == '\r') {
				int currentRange = this.currentRange;
				++currentOffset;
				
				while (current[currentOffset] == '\n') {
					if (currentOffset == currentRange) {
						if ((currentRange = this.fill()) == 0) {
							return null;
						}

						currentOffset = 0;
					} else {
						++currentOffset;
						
						if(current[currentOffset] == '\r') {
							++currentOffset;
						}
					}
				}
			}					
			*/
			final int rangeVariableIndex = 3;

			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 13);
			Label l5 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l5);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, subClassInternalName, "currentRange", "I");
			mv.visitVarInsn(ISTORE, 3);
			mv.visitIincInsn(currentOffsetIndex, 1);
			Label l8 = new Label();
			mv.visitLabel(l8);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 10);
			mv.visitJumpInsn(IF_ICMPNE, l5);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, rangeVariableIndex);
			Label l10 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l10);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "fill", "()I", false);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ISTORE, rangeVariableIndex);
			Label l12 = new Label();
			mv.visitJumpInsn(IFNE, l12);
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(ARETURN);
			mv.visitLabel(l12);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
			mv.visitJumpInsn(GOTO, l8);
			mv.visitLabel(l10);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 13);
			mv.visitJumpInsn(IF_ICMPNE, l8);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitJumpInsn(GOTO, l8);
			mv.visitLabel(l5);
		}
	}

	protected void staticInitializer(ClassWriter classWriter, AbstractColumn[] columns, String classInternalName, String className) {
		{
			MethodVisitor mv = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
			mv.visitCode();
			
			Label startLabel = new Label();
			mv.visitLabel(startLabel);
			
			int consumerArrayIndex = 1;

			mv.visitLdcInsn(className);
			mv.visitMethodInsn(INVOKESTATIC, csvStaticInitializer, "remove", "(Ljava/lang/String;)Lcom/github/skjolber/stcsv/CsvReaderStaticInitializer$CsvStaticFields;", false);
			mv.visitVarInsn(ASTORE, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/skjolber/stcsv/CsvReaderStaticInitializer$CsvStaticFields", "getConsumers", "()[L" + consumerName + ";", false);
			mv.visitVarInsn(ASTORE, consumerArrayIndex);

			// consumers
			for (int k = 0; k < columns.length; k++) {
				if(columns[k] != null && columns[k].isConsumer()) {
					mv.visitVarInsn(ALOAD, consumerArrayIndex);
					mv.visitIntInsn(BIPUSH, k);
					mv.visitInsn(AALOAD);
					mv.visitTypeInsn(CHECKCAST, columns[k].getConsumerInternalName());
			    	mv.visitFieldInsn(PUTSTATIC, classInternalName, "v" + columns[k].getIndex(), "L" + columns[k].getConsumerInternalName() + ";");
				}
			}
			
			Label endLabel = new Label();
			mv.visitLabel(endLabel);
			mv.visitInsn(RETURN);
			mv.visitLocalVariable("fields", "Lcom/github/skjolber/stcsv/CsvReaderStaticInitializer$CsvStaticFields;", null, startLabel, endLabel, 0);
			mv.visitLocalVariable("consumerList", "[L" + consumerName + ";", null, startLabel, endLabel, consumerArrayIndex);
			mv.visitMaxs(0, 0);
			mv.visitEnd();		    	
		}
	}

	protected void fields(ClassWriter classWriter, AbstractColumn[] mapping) {
		// static final fields
		for (int k = 0; k < mapping.length; k++) {
			if(mapping[k] != null && mapping[k].isConsumer()) {
		    	classWriter
		    		.visitField(ACC_STATIC + ACC_PRIVATE + ACC_FINAL, "v" + mapping[k].getIndex(), "L" + mapping[k].getConsumerInternalName() + ";", null, null)
		    		.visitEnd();
			}
		}
	}

	protected void constructor(ClassWriter classWriter, String subClassInternalName) {
		{
			MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/io/Reader;)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(new Integer(bufferLength));
			mv.visitMethodInsn(INVOKESPECIAL, superClassInternalName, "<init>", "(Ljava/io/Reader;I)V", false);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "L" + subClassInternalName + ";", null, l0, l2, 0);
			mv.visitLocalVariable("reader", "Ljava/io/Reader;", null, l0, l2, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}

		{
			MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/io/Reader;[CII)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ILOAD, 3);
			mv.visitVarInsn(ILOAD, 4);
			mv.visitMethodInsn(INVOKESPECIAL, superClassInternalName, "<init>", "(Ljava/io/Reader;[CII)V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "L" + subClassInternalName + ";", null, l0, l2, 0);
			mv.visitLocalVariable("reader", "Ljava/io/Reader;", null, l0, l2, 1);
			mv.visitLocalVariable("current", "[C", null, l0, l2, 2);
			mv.visitLocalVariable("offset", "I", null, l0, l2, 3);
			mv.visitLocalVariable("length", "I", null, l0, l2, 4);
			mv.visitMaxs(5, 5);
			mv.visitEnd();
		}
	}

	protected String parseStaticFieldName(Class<?> cls) {
		String simpleName = cls.getSimpleName();
		
		return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
	}
	
	protected void saveCurrentOffset(MethodVisitor mv, String superClassInternalName, int currentOffsetIndex) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitFieldInsn(PUTFIELD, superClassInternalName, "currentOffset", "I");		
	}

	protected List<String> parseNames(String writer) {
		List<String> names = new ArrayList<>();
		int start = 0;
		for(int i = 0; i < writer.length(); i++) {
			if(writer.charAt(i) == divider) {
				String trim = writer.substring(start, i).trim();
				if(!trim.isEmpty() && trim.charAt(0) == '"' && trim.charAt(trim.length() - 1) == '"') {
					names.add(trim.substring(1, trim.length() - 1));
				} else {
					names.add(trim);
				}
				start = i + 1;
			}
		}
		
		if(start < writer.length()) {
			String trim = writer.substring(start, writer.length()).trim();
			if(trim.charAt(0) == '"' && trim.charAt(trim.length() - 1) == '"') {
				names.add(trim.substring(1, trim.length() - 1));
			} else {
				names.add(trim);
			}
		}
		return names;
	}
	
	public String getMappedClassInternalName() {
		return mappedClassInternalName;
	}
}
