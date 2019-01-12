package com.github.skjolber.stcsv.projection;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.AbstractCsvMapper;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.column.bi.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.bi.StringCsvColumnValueConsumer;

public class BiConsumerProjection implements ValueProjection {

	protected static final int currentOffsetIndex = AbstractCsvMapper.VAR_CURRENT_OFFSET;
	protected static final int currentArrayIndex = AbstractCsvMapper.VAR_CURRENT_ARRAY;
	protected static final int objectIndex = AbstractCsvMapper.VAR_OBJECT;
	protected static final int startIndex = AbstractCsvMapper.VAR_START;
	protected static final int rangeIndex = AbstractCsvMapper.VAR_RANGE;
	protected static final int intermediateIndex = AbstractCsvMapper.VAR_INTERMEDIATE_OBJECT;
	
	public static final String biConsumerName = CsvMapper.getInternalName(CsvColumnValueConsumer.class);

	protected final String biConsumerInternalName;
	protected final CsvColumnValueConsumer<?> biConsumer;
	protected final int index;
	
	public BiConsumerProjection(CsvColumnValueConsumer<?> biConsumer, int index) {
		super();
		this.biConsumer = biConsumer;
		this.index = index;
		
		if(biConsumer.getClass().getPackage().equals(StringCsvColumnValueConsumer.class.getPackage())) {
			biConsumerInternalName = CsvMapper.getInternalName(biConsumer.getClass());
		} else {
			biConsumerInternalName = biConsumerName;
		}
	}

	@Override
	public void write(MethodVisitor mv, String subClassInternalName, int endIndex) {
		mv.visitFieldInsn(GETSTATIC, subClassInternalName, "v" + index, "L" + biConsumerInternalName + ";");
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ILOAD, endIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, biConsumerInternalName, "consume", "(Ljava/lang/Object;[CII)V", false);
	}

	public String getBiConsumerInternalName() {
		return biConsumerInternalName;
	}
	
	public CsvColumnValueConsumer<?> getBiConsumer() {
		return biConsumer;
	}
}
