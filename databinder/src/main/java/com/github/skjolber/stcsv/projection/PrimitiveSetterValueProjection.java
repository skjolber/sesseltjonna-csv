package com.github.skjolber.stcsv.projection;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.MethodVisitor;

public class PrimitiveSetterValueProjection extends SetterValueProjection {

    private final String owner;
    private final String name;
    private final char type;
	
	public PrimitiveSetterValueProjection(String setterName, String mappedClassInternalName, String owner, String name, char type) {
		super(setterName, mappedClassInternalName);
		this.owner = owner;
		this.name = name;
		this.type = type;
	}

	@Override
	public void write(MethodVisitor mv, String subClassInternalName, int endIndex) {
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ILOAD, endIndex);
		mv.visitMethodInsn(INVOKESTATIC, owner, name, "([CII)" + type, false);
		mv.visitMethodInsn(INVOKEVIRTUAL, mappedClassInternalName, setterName, "(" + type + ")V", false);		
	}

}
