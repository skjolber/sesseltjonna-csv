package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
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

import java.io.File;
import java.io.FileOutputStream;
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
import com.github.skjolber.stcsv.column.bi.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.tri.CsvColumnValueTriConsumer;
import com.github.skjolber.stcsv.projection.BiConsumerProjection;
import com.github.skjolber.stcsv.projection.TriConsumerProjection;

/**
 * 
 * Dynamic CSV parser generator. Adapts the underlying implementation according 
 * to the first (header) line.
 * <br><br>
 * Uses ASM to build the parsers.
 * <br><br>
 * Thread-safe.
 */

public abstract class AbstractCsvMapper<T> {

	public static final int VAR_CURRENT_OFFSET = 1;
	public static final int VAR_CURRENT_ARRAY = 2;
	public static final int VAR_OBJECT = 3;
	public static final int VAR_START = 4;
	public static final int VAR_RANGE = 5;
	public static final int VAR_INTERMEDIATE_OBJECT = 6;
	
	protected static final String GENERATED_CLASS_SIMPLE_NAME = "GeneratedCsvClassFactory%d";
	protected static final String GENERATED_CLASS_FULL_NAME = "com.github.skjolber.stcsv." + GENERATED_CLASS_SIMPLE_NAME;
	protected static final String GENERATED_CLASS_FULL_INTERNAL = "com/github/skjolber/stcsv/" + GENERATED_CLASS_SIMPLE_NAME;

	protected static final String superClassInternalName = getInternalName(AbstractCsvReader.class);
	protected static final String csvStaticInitializer = getInternalName(CsvReaderStaticInitializer.class);
	protected static final String ignoredSeperateQuoteAndEscapeCharacterColumnName = getInternalName(IgnoredColumn.DifferentQuoteAndEscapeCharacter.class);
	protected static final String ignoredIdenticalQuoteAndEscapeCharacterColumnName = getInternalName(IgnoredColumn.IdenticalQuoteAndEscapeCharacter.class);
	
	protected static AtomicInteger counter = new AtomicInteger();

	public static String getInternalName(Class<?> cls) {
		return getInternalName(cls.getName());
	}

	public static String getInternalName(String className) {
		return className.replace('.', '/');
	}

	protected int divider;
	protected int quoteCharacter;
	protected int escapeCharacter;
	protected Class<T> mappedClass;

	protected String mappedClassInternalName;

	protected Map<String, AbstractColumn> keys = new HashMap<>(); // thread safe for reading
	protected List<AbstractColumn> columns;

	protected final boolean skipEmptyLines;
	protected final boolean skipComments;
	protected final boolean skippableFieldsWithoutLinebreaks;
	protected final int bufferLength;

	/**
	 * Note: Stack variable types are fixed throughout the application, as below. 
	 * 
	 * The range index is dual purpose as end index for trimming quoted content.
	 * 
	 */

	protected final static int currentOffsetIndex = VAR_CURRENT_OFFSET;
	protected final static int currentArrayIndex = VAR_CURRENT_ARRAY;
	protected final static int objectIndex = VAR_OBJECT;
	protected final static int startIndex = VAR_START;
	protected final static int rangeIndex = VAR_RANGE;
	protected final static int intermediateIndex = VAR_INTERMEDIATE_OBJECT;

	protected final Map<String, DefaultStaticCsvMapper<T>> factories = new ConcurrentHashMap<>();
	protected final ClassLoader classLoader;
	
	protected final boolean biConsumer;
	protected final boolean triConsumer;

	public AbstractCsvMapper(Class<T> cls, char divider, char quoteCharacter, char escapeCharacter, List<AbstractColumn> columns, boolean skipEmptyLines, boolean skipComments, boolean skippableFieldsWithoutLinebreaks, ClassLoader classLoader, int bufferLength) {
		this.mappedClass = cls;
		this.divider = divider;
		this.quoteCharacter = quoteCharacter;
		this.escapeCharacter = escapeCharacter;
		this.columns = columns;

		this.skipEmptyLines = skipEmptyLines;
		this.skipComments = skipComments;
		this.skippableFieldsWithoutLinebreaks = skippableFieldsWithoutLinebreaks;
		this.classLoader = classLoader;
		this.bufferLength = bufferLength;

		boolean biConsumer = false;
		boolean triConsumer = false;
		
		for (AbstractColumn column : columns) {
			keys.put(column.getName(),  column);

			column.setParent(this);
			
			if(column.isBiConsumer()) {
				biConsumer = true;
			}
			
			if(column.isTriConsumer()) {
				triConsumer = true;
			}
		}

		this.mappedClassInternalName = getInternalName(mappedClass);
		
		this.biConsumer = biConsumer;
		this.triConsumer = triConsumer;
	}

	protected int getDivider() {
		return divider;
	}

	public Class<? extends AbstractCsvReader<T>> createDefaultReaderClass(boolean carriageReturns) throws Exception {
		List<String> names = new ArrayList<>();
		for (AbstractColumn column: columns) {
			names.add(column.getName());
		}
		return createReaderClass(carriageReturns, names);
	}

	public Class<? extends AbstractCsvReader<T>> createReaderClass(boolean carriageReturns, String header) throws Exception {
		return createReaderClass(carriageReturns, parseColumnNames(header));
	}

	public Class<? extends AbstractCsvReader<T>> createReaderClass(boolean carriageReturns, List<String> csvFileFieldNames) throws Exception {
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
		return loader.load(classWriter.toByteArray(), subClassName);
	}

	protected String write(ClassWriter classWriter, List<String> csvFileFieldNames, boolean carriageReturns) {
		int subclassNumber = counter.incrementAndGet();
		String subClassName = String.format(GENERATED_CLASS_FULL_NAME, subclassNumber);
		String subClassInternalName = String.format(GENERATED_CLASS_FULL_INTERNAL, subclassNumber);

		AbstractColumn[] mapping = new AbstractColumn[csvFileFieldNames.size()]; 

		CsvColumnValueConsumer<?>[] biConsumers = new CsvColumnValueConsumer[mapping.length];
		CsvColumnValueTriConsumer<?, ?>[] triConsumers = new CsvColumnValueTriConsumer[mapping.length];

		boolean inline = true;

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

				if(field.isBiConsumer()) {
					biConsumers[j] = ((BiConsumerProjection)field.getProjection()).getBiConsumer();
				} else if(field.isTriConsumer()) {
					triConsumers[j] = ((TriConsumerProjection)field.getProjection()).getTriConsumer();
				}
				
				lastIndex = j;
			}
		}

		if(lastIndex == -1) {
			return null;
		}

		// generics seems to not work when generating multiple classes; 
		// fails for class number 2 because of failing method signature
		// TODO still generate such a beast for the first?
		classWriter.visit(Opcodes.V1_8,
				ACC_FINAL | ACC_PUBLIC,
				subClassInternalName,
				null,
				superClassInternalName,
				null);

		if(biConsumer || triConsumer) {
			// place in-scope values which will be read by static initializer
			CsvReaderStaticInitializer.add(subClassName, biConsumers, triConsumers);

			// static initializer and fields
			addStatics(classWriter, mapping, subClassInternalName, subClassName);
		}

		// constructor with reader
		addConstructors(classWriter, subClassInternalName);

		// parse main method
		addMethod(classWriter, subClassInternalName, mapping, carriageReturns, inline, lastIndex, firstIndex);

		classWriter.visitEnd();
		return subClassName;
	}

	protected void addMethod(ClassWriter classWriter, String subClassInternalName, AbstractColumn[] mapping,
			boolean carriageReturns, boolean inline, int lastIndex, int firstIndex) {
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "next", "()Ljava/lang/Object;", null, new String[] { "java/io/IOException" });

		mv.visitCode();
		Label startLabel = new Label();
		mv.visitLabel(startLabel);

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

		Label endLabel = new Label();

		Label exceptionHandling = new Label();
		mv.visitTryCatchBlock(startTryCatch, endLabel, exceptionHandling, "java/lang/ArrayIndexOutOfBoundsException");

		mv.visitLabel(startTryCatch);

		if(skipEmptyLines && skipComments) {
			writeSkipEmptyOrCommentedLines(mv, subClassInternalName, carriageReturns);
		} else if(skipEmptyLines) {
			writeSkipEmptyLines(mv, subClassInternalName, carriageReturns);
		} else if(skipComments) {
			writeSkipComments(mv, subClassInternalName);
		}

		// init value object, i.e. the object to which data-binding will occur
		mv.visitTypeInsn(NEW, mappedClassInternalName);
		mv.visitInsn(DUP); // add one
		mv.visitMethodInsn(INVOKESPECIAL, mappedClassInternalName, "<init>", "()V", false); // consumes one
		mv.visitVarInsn(ASTORE, objectIndex);

		if(firstIndex > 0) {
			// skip first column(s)
			skipColumns(mv, firstIndex);
		}

		// don't introduce the intermediate processor variable 
		// before it is necessary
		boolean wroteTriConsumer = false;

		int current = firstIndex;
		do {
			AbstractColumn column = mapping[current];
			if(column.isTriConsumer() && !wroteTriConsumer) {
				writeTriConsumerVariable(subClassInternalName, mv);

				wroteTriConsumer = true;
			}
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

		mv.visitLabel(endLabel);

		// catch / rethrow block
		// https://stackoverflow.com/questions/12438567/java-bytecode-dup
		mv.visitLabel(exceptionHandling);
		mv.visitVarInsn(ASTORE, 1); // store exception
		mv.visitTypeInsn(NEW, "com/github/skjolber/stcsv/CsvException");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 1); // load exception
		mv.visitMethodInsn(INVOKESPECIAL, "com/github/skjolber/stcsv/CsvException", "<init>", "(Ljava/lang/Throwable;)V", false);
		mv.visitInsn(ATHROW);

		// finish up method
		mv.visitLocalVariable("this", "L" + subClassInternalName + ";", null, startLabel, endLabel, 0);
		mv.visitLocalVariable("value", "L" + mappedClassInternalName + ";", null, startLabel, endLabel, objectIndex);
		mv.visitLocalVariable("currentOffset", "I", null, startLabel, endLabel, currentOffsetIndex);
		mv.visitLocalVariable("current", "[C", null, startLabel, endLabel, currentArrayIndex);
		if(inline) {
			mv.visitLocalVariable("start", "I", null, startLabel, endLabel, startIndex);
			mv.visitLocalVariable("rangeIndex", "I", null, startLabel, endLabel, rangeIndex);
			
			mv.visitMaxs(7, 6);
		} else {
			mv.visitMaxs(7, 4);
		}
		mv.visitEnd();
	}

	protected abstract void writeTriConsumerVariable(String subClassInternalName, MethodVisitor mv);

	protected void addConstructors(ClassWriter classWriter, String subClassInternalName) {
		addConstructors(classWriter, subClassInternalName, null);
	}

	protected void writeSkipComments(MethodVisitor mv, String subClassInternalName) {
		final int rangeVariableIndex = 3;

		Label l12 = new Label();
		mv.visitJumpInsn(GOTO, l12);
		Label l13 = new Label();
		mv.visitLabel(l13);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, subClassInternalName, "currentRange", "I");
		mv.visitVarInsn(ISTORE, rangeVariableIndex);
		Label l14 = new Label();
		mv.visitLabel(l14);
		mv.visitIincInsn(currentOffsetIndex, 1);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, 10);
		mv.visitJumpInsn(IF_ICMPNE, l14);
		Label l16 = new Label();
		mv.visitLabel(l16);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitVarInsn(ILOAD, rangeVariableIndex);
		Label l17 = new Label();
		mv.visitJumpInsn(IF_ICMPNE, l17);
		Label l18 = new Label();
		mv.visitLabel(l18);
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
		mv.visitJumpInsn(GOTO, l12);
		mv.visitLabel(l17);
		mv.visitIincInsn(currentOffsetIndex, 1);
		mv.visitLabel(l12);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, 35); // #
		mv.visitJumpInsn(IF_ICMPEQ, l13);
	}

	protected void skipToLinebreak(MethodVisitor mv) {
		if(quoteCharacter == escapeCharacter) {
			if(skippableFieldsWithoutLinebreaks) {
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitLdcInsn(Integer.valueOf(escapeCharacter));
				mv.visitMethodInsn(INVOKESTATIC, ignoredIdenticalQuoteAndEscapeCharacterColumnName, "skipToLineBreakWithoutLinebreak", "([CII)I", false);
				mv.visitVarInsn(ISTORE, currentOffsetIndex);
			} else {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitLdcInsn(Integer.valueOf(escapeCharacter));
				mv.visitMethodInsn(INVOKESTATIC, ignoredIdenticalQuoteAndEscapeCharacterColumnName, "skipToLineBreak", "(L" + superClassInternalName + ";[CII)I", false);
				mv.visitVarInsn(ISTORE, currentOffsetIndex);
			}
		} else {
			
			if(skippableFieldsWithoutLinebreaks) {
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitLdcInsn(Integer.valueOf(divider));
				mv.visitLdcInsn(Integer.valueOf(quoteCharacter));
				mv.visitLdcInsn(Integer.valueOf(escapeCharacter));
				mv.visitMethodInsn(INVOKESTATIC, ignoredSeperateQuoteAndEscapeCharacterColumnName, "skipToLineBreakWithoutLinebreak", "([CIII)I", false);
				mv.visitVarInsn(ISTORE, currentOffsetIndex);
			} else {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitLdcInsn(Integer.valueOf(quoteCharacter));
				mv.visitLdcInsn(Integer.valueOf(escapeCharacter));
				mv.visitMethodInsn(INVOKESTATIC, ignoredSeperateQuoteAndEscapeCharacterColumnName, "skipToLineBreak", "(L" + superClassInternalName + ";[CIII)I", false);
				mv.visitVarInsn(ISTORE, currentOffsetIndex);
			}			
		}
	}

	protected void skipColumns(MethodVisitor mv, int count) {
		if(quoteCharacter == escapeCharacter) {
			if(skippableFieldsWithoutLinebreaks) {
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitLdcInsn(Integer.valueOf(divider));
				mv.visitLdcInsn(Integer.valueOf(escapeCharacter));
				mv.visitLdcInsn(Integer.valueOf(count));
				mv.visitMethodInsn(INVOKESTATIC, ignoredIdenticalQuoteAndEscapeCharacterColumnName, "skipColumnsWithoutLinebreak", "([CIIII)I", false);
				mv.visitVarInsn(ISTORE, currentOffsetIndex);
			} else {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitLdcInsn(Integer.valueOf(divider));
				mv.visitLdcInsn(Integer.valueOf(escapeCharacter));
				mv.visitLdcInsn(Integer.valueOf(count));
				mv.visitMethodInsn(INVOKESTATIC, ignoredIdenticalQuoteAndEscapeCharacterColumnName, "skipColumns", "(L" + superClassInternalName + ";[CIIII)I", false);
				mv.visitVarInsn(ISTORE, currentOffsetIndex);
			}
		} else {
			if(skippableFieldsWithoutLinebreaks) {
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitLdcInsn(Integer.valueOf(divider));
				mv.visitLdcInsn(Integer.valueOf(quoteCharacter));
				mv.visitLdcInsn(Integer.valueOf(escapeCharacter));
				mv.visitLdcInsn(Integer.valueOf(count));
				mv.visitMethodInsn(INVOKESTATIC, ignoredSeperateQuoteAndEscapeCharacterColumnName, "skipColumnsWithoutLinebreak", "([CIIIII)I", false);
				mv.visitVarInsn(ISTORE, currentOffsetIndex);
			} else {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitLdcInsn(Integer.valueOf(divider));
				mv.visitLdcInsn(Integer.valueOf(quoteCharacter));
				mv.visitLdcInsn(Integer.valueOf(escapeCharacter));
				mv.visitLdcInsn(Integer.valueOf(count));
				mv.visitMethodInsn(INVOKESTATIC, ignoredSeperateQuoteAndEscapeCharacterColumnName, "skipColumns", "(L" + superClassInternalName + ";[CIIIII)I", false);
				mv.visitVarInsn(ISTORE, currentOffsetIndex);
			}
			
		}
	}

	protected void writeSkipEmptyOrCommentedLines(MethodVisitor mv, String subClassInternalName, boolean carriageReturns) {
		/*

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
			
		
		*/
		final int rangeVariableIndex = 3;

		Label l12 = new Label();
		mv.visitJumpInsn(GOTO, l12);
		Label l13 = new Label();
		mv.visitLabel(l13);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, subClassInternalName, "currentRange", "I");
		mv.visitVarInsn(ISTORE, rangeVariableIndex);
		Label l14 = new Label();
		mv.visitLabel(l14);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, 10);
		Label l15 = new Label();
		mv.visitJumpInsn(IF_ICMPNE, l15);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitVarInsn(ILOAD, rangeVariableIndex);
		Label l17 = new Label();
		mv.visitJumpInsn(IF_ICMPNE, l17);
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
		mv.visitJumpInsn(GOTO, l12);
		mv.visitLabel(l17);
		mv.visitIincInsn(currentOffsetIndex, 1);
		mv.visitJumpInsn(GOTO, l12);
		mv.visitLabel(l15);
		mv.visitIincInsn(currentOffsetIndex, 1);
		mv.visitJumpInsn(GOTO, l14);
		mv.visitLabel(l12);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, 35); // #
		mv.visitJumpInsn(IF_ICMPEQ, l13);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, carriageReturns ? 13: 10); // n or r
		mv.visitJumpInsn(IF_ICMPEQ, l13);

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

	protected void addStatics(ClassWriter classWriter, AbstractColumn[] columns, String classInternalName, String className) {
		MethodVisitor mv = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitCode();

		Label startLabel = new Label();
		mv.visitLabel(startLabel);

		final int biConsumerArrayIndex = 2;
		final int triConsumerArrayIndex = 3;

		mv.visitLdcInsn(className);
		mv.visitMethodInsn(INVOKESTATIC, csvStaticInitializer, "remove", "(Ljava/lang/String;)Lcom/github/skjolber/stcsv/CsvReaderStaticInitializer$CsvStaticFields;", false);
		mv.visitVarInsn(ASTORE, 0);
		
		if(biConsumer) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/skjolber/stcsv/CsvReaderStaticInitializer$CsvStaticFields", "getBiConsumers", "()[L" + BiConsumerProjection.biConsumerName + ";", false);
			mv.visitVarInsn(ASTORE, biConsumerArrayIndex);
		}
		if(triConsumer) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/skjolber/stcsv/CsvReaderStaticInitializer$CsvStaticFields", "getTriConsumers", "()[L" + TriConsumerProjection.triConsumerName + ";", false);
			mv.visitVarInsn(ASTORE, triConsumerArrayIndex);
		}
		
		// consumers
		for (int k = 0; k < columns.length; k++) {
			if(columns[k] != null) {
				String consumerInternalName;
				if(columns[k].isBiConsumer()) {
					BiConsumerProjection biConsumerProjection = (BiConsumerProjection)columns[k].getProjection();
					consumerInternalName = biConsumerProjection.getBiConsumerInternalName();
					
					// write source array
					mv.visitVarInsn(ALOAD, biConsumerArrayIndex);
				} else if(columns[k].isTriConsumer()) {
					TriConsumerProjection triConsumerProjection = (TriConsumerProjection)columns[k].getProjection();
					consumerInternalName = triConsumerProjection.getTriConsumerInternalName();
					
					// write source array
					mv.visitVarInsn(ALOAD, triConsumerArrayIndex);
				} else {
					continue;
				}
				
				String fieldName = "v" + columns[k].getIndex();
				String fieldDescriptor = "L" + consumerInternalName + ";";

				// write static field
				classWriter
					.visitField(ACC_STATIC + ACC_PRIVATE + ACC_FINAL, fieldName, fieldDescriptor, null, null)
					.visitEnd();
				
				// write field assignment
				mv.visitLdcInsn(Integer.valueOf(k));
				mv.visitInsn(AALOAD);
				mv.visitTypeInsn(CHECKCAST, consumerInternalName);
				mv.visitFieldInsn(PUTSTATIC, classInternalName, fieldName, fieldDescriptor);
			}
		}

		Label endLabel = new Label();
		mv.visitLabel(endLabel);
		mv.visitInsn(RETURN);
		mv.visitLocalVariable("fields", "Lcom/github/skjolber/stcsv/CsvReaderStaticInitializer$CsvStaticFields;", null, startLabel, endLabel, 0);
		if(biConsumer) {
			mv.visitLocalVariable("biConsumerList", "[L" + BiConsumerProjection.biConsumerName + ";", null, startLabel, endLabel, biConsumerArrayIndex);
		}
		if(triConsumer) {
			mv.visitLocalVariable("triConsumerList", "[L" + TriConsumerProjection.triConsumerName + ";", null, startLabel, endLabel, triConsumerArrayIndex);
		}
		if(triConsumer && biConsumer) {
			mv.visitMaxs(2, 4);
		} else {
			mv.visitMaxs(2, 3);
		}
		mv.visitEnd();		    	
	}

	protected void addConstructors(ClassWriter classWriter, String subClassInternalName, String intermediateInternalName) {
		writeReaderConstructor(classWriter, subClassInternalName, intermediateInternalName);
		writeReaderWithBufferConstructor(classWriter, subClassInternalName, intermediateInternalName);
	}

	protected void writeReaderWithBufferConstructor(ClassWriter classWriter, String subClassInternalName,
			String intermediateInternalName) {
		// write simple Reader constructor with offset and range
		String signature;
		if(intermediateInternalName == null) {
			signature = "(Ljava/io/Reader;[CII)V";
		} else {
			signature = "(Ljava/io/Reader;[CIIL" + intermediateInternalName + ";)V";
		}

		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", signature, null, null);
		mv.visitCode();
		Label startLabel = new Label();
		mv.visitLabel(startLabel);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ILOAD, 3);
		mv.visitVarInsn(ILOAD, 4);
		mv.visitMethodInsn(INVOKESPECIAL, superClassInternalName, "<init>", "(Ljava/io/Reader;[CII)V", false);
		
		if(intermediateInternalName != null) {
			classWriter
				.visitField(ACC_PRIVATE + ACC_FINAL, "intermediate", "L" + intermediateInternalName + ";", null, null)
				.visitEnd();

			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 5);
			mv.visitFieldInsn(PUTFIELD, subClassInternalName, "intermediate", "L" + intermediateInternalName + ";");				
		}
		
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitInsn(RETURN);
		Label endLabel = new Label();
		mv.visitLabel(endLabel);
		mv.visitLocalVariable("this", "L" + subClassInternalName + ";", null, startLabel, endLabel, 0);
		mv.visitLocalVariable("reader", "Ljava/io/Reader;", null, startLabel, endLabel, 1);
		mv.visitLocalVariable("current", "[C", null, startLabel, endLabel, 2);
		mv.visitLocalVariable("offset", "I", null, startLabel, endLabel, 3);
		mv.visitLocalVariable("length", "I", null, startLabel, endLabel, 4);
		
		if(intermediateInternalName != null) {
			mv.visitLocalVariable("intermediateInternalName", "Ljava/lang/String;", null, startLabel, endLabel, 5);
			mv.visitMaxs(5, 6);
		} else {
			mv.visitMaxs(5, 5);
		}

		mv.visitEnd();
	}

	protected void writeReaderConstructor(ClassWriter classWriter, String subClassInternalName,
			String intermediateInternalName) {
		// write simple Reader constructor 
		// 
		String signature;
		if(intermediateInternalName == null) {
			signature = "(Ljava/io/Reader;)V";
		} else {
			signature = "(Ljava/io/Reader;L" + intermediateInternalName + ";)V";
		}
		
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", signature, null, null);
		mv.visitCode();
		
		Label startLabel = new Label();
		mv.visitLabel(startLabel);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitLdcInsn(Integer.valueOf(bufferLength));
		mv.visitMethodInsn(INVOKESPECIAL, superClassInternalName, "<init>", "(Ljava/io/Reader;I)V", false);
		
		if(intermediateInternalName != null) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitFieldInsn(PUTFIELD, subClassInternalName, "intermediate", "L" + intermediateInternalName + ";");				
		}
		
		mv.visitInsn(RETURN);
		Label endLabel = new Label();
		mv.visitLabel(endLabel);
		mv.visitLocalVariable("this", "L" + subClassInternalName + ";", null, startLabel, endLabel, 0);
		mv.visitLocalVariable("reader", "Ljava/io/Reader;", null, startLabel, endLabel, 1);
		if(intermediateInternalName != null) {
			mv.visitLocalVariable("intermediateInternalName", "Ljava/lang/String;", null, startLabel, endLabel, 2);
			
			mv.visitMaxs(3, 3);
		} else {
			mv.visitMaxs(3, 2);
		}
		
		mv.visitEnd();
	}

	protected void saveCurrentOffset(MethodVisitor mv, String superClassInternalName, int currentOffsetIndex) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitFieldInsn(PUTFIELD, superClassInternalName, "currentOffset", "I");		
	}

	protected List<String> parseColumnNames(String writer) {
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
	
	ClassLoader getClassLoader() {
		return classLoader;
	}
	
}
