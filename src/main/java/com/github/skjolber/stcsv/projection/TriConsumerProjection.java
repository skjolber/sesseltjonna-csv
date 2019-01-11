package com.github.skjolber.stcsv.projection;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.AbstractCsvMapper;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.column.bi.CsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.bi.StringCsvColumnValueConsumer;
import com.github.skjolber.stcsv.column.tri.CsvColumnValueTriConsumer;
import com.github.skjolber.stcsv.column.tri.StringCsvColumnValueTriConsumer;

public class TriConsumerProjection implements ValueProjection {

	public static final String triConsumerName = CsvMapper.getInternalName(CsvColumnValueTriConsumer.class);

	protected static final int currentOffsetIndex = AbstractCsvMapper.VAR_CURRENT_OFFSET;
	protected static final int currentArrayIndex = AbstractCsvMapper.VAR_CURRENT_ARRAY;
	protected static final int objectIndex = AbstractCsvMapper.VAR_OBJECT;
	protected static final int startIndex = AbstractCsvMapper.VAR_START;
	protected static final int rangeIndex = AbstractCsvMapper.VAR_RANGE;
	protected static final int intermediateIndex = AbstractCsvMapper.VAR_INTERMEDIATE_OBJECT;
	
	protected final String triConsumerInternalName;
	protected final CsvColumnValueTriConsumer<?, ?> triConsumer;
	protected final int index;
	
	public TriConsumerProjection(CsvColumnValueTriConsumer<?, ?> triConsumer, int index) {
		super();
		this.triConsumer = triConsumer;
		this.index = index;
		
		if(triConsumer.getClass().getPackage().equals(StringCsvColumnValueTriConsumer.class.getPackage())) {
			// specific subclass
			triConsumerInternalName = CsvMapper.getInternalName(triConsumer.getClass());
		} else {
			triConsumerInternalName = triConsumerName;
		}
	}

	@Override
	public void write(MethodVisitor mv, String subClassInternalName, int endIndex) {
		mv.visitFieldInsn(GETSTATIC, subClassInternalName, "v" + index, "L" + triConsumerInternalName + ";");
		
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitVarInsn(ALOAD, intermediateIndex);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ILOAD, endIndex);
		mv.visitMethodInsn(INVOKEVIRTUAL, triConsumerInternalName, "consume", "(Ljava/lang/Object;Ljava/lang/Object;[CII)V", false);
	}

	public String getTriConsumerInternalName() {
		return triConsumerInternalName;
	}
	
	public CsvColumnValueTriConsumer<?, ?> getTriConsumer() {
		return triConsumer;
	}
}
