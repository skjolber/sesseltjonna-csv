package com.github.skjolber.stcsv.projection;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISUB;
import static org.objectweb.asm.Opcodes.NEW;

import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.AbstractCsvMapper;

public class SetterValueProjection implements ValueProjection {

	protected static final int currentOffsetIndex = AbstractCsvMapper.VAR_CURRENT_OFFSET;
	protected static final int currentArrayIndex = AbstractCsvMapper.VAR_CURRENT_ARRAY;
	protected static final int objectIndex = AbstractCsvMapper.VAR_OBJECT;
	protected static final int startIndex = AbstractCsvMapper.VAR_START;
	protected static final int rangeIndex = AbstractCsvMapper.VAR_RANGE;
	protected static final int intermediateIndex = AbstractCsvMapper.VAR_INTERMEDIATE_OBJECT;

	protected String setterName;
	protected Class<?> setterClass;
	protected String mappedClassInternalName;
	
	public SetterValueProjection(String setterName, Class<?> setterClass, String mappedClassInternalName) {
		this.setterName = setterName;
		this.setterClass = setterClass;
		this.mappedClassInternalName = mappedClassInternalName;
	}
	
	protected void doubleSetter(MethodVisitor mv, int endIndex) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ILOAD, endIndex);
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/stcsv/column/bi/DoubleCsvColumnValueConsumer", "parseDouble", "([CII)D", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, mappedClassInternalName, setterName, "(D)V", false);		
	}

	protected void booleanSetter(MethodVisitor mv, int endIndex) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ILOAD, endIndex);
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/stcsv/column/bi/BooleanCsvColumnValueConsumer", "parseBoolean", "([CII)Z", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, mappedClassInternalName, setterName, "(Z)V", false);		
	}

	protected void longSetter(MethodVisitor mv, int endIndex) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ILOAD, endIndex);
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/stcsv/column/bi/LongCsvColumnValueConsumer", "parseLong", "([CII)J", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, mappedClassInternalName, setterName, "(J)V", false);		
	}	
	
	protected void intSetter(MethodVisitor mv, int endIndex) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ILOAD, endIndex);
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/stcsv/column/bi/IntCsvColumnValueConsumer", "parseInt", "([CII)I", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, mappedClassInternalName, setterName, "(I)V", false);		
	}

	protected void stringSetter(MethodVisitor mv, int endIndex) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitTypeInsn(NEW, "java/lang/String");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ILOAD, endIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitInsn(ISUB);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([CII)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, mappedClassInternalName, setterName, "(Ljava/lang/String;)V", false);
	}

	@Override
	public void write(MethodVisitor mv, String subClassInternalName, int endIndex) {
		if(setterClass == String.class) {
			stringSetter(mv, endIndex);
		} else if(setterClass == int.class) {
			intSetter(mv, endIndex);
		} else if(setterClass == long.class) {
			longSetter(mv, endIndex);
		} else if(setterClass == boolean.class) {
			booleanSetter(mv, endIndex);
		} else if(setterClass == double.class) {
			doubleSetter(mv, endIndex);
		} else {
			throw new RuntimeException("No setter for " + getClass().getName());
		}
	}

}
