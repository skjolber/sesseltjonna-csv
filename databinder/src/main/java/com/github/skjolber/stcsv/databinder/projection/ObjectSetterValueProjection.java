package com.github.skjolber.stcsv.databinder.projection;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISUB;
import static org.objectweb.asm.Opcodes.NEW;

import org.objectweb.asm.MethodVisitor;

/**
 * Setter for objects. 
 *
 */

public class ObjectSetterValueProjection extends SetterValueProjection {

    private final String typeInternalName;
	
	public ObjectSetterValueProjection(String setterName, String mappedClassInternalName, String typeInternalName) {
		super(setterName, mappedClassInternalName);
		this.typeInternalName = typeInternalName;
	}

	@Override
	public void write(MethodVisitor mv, String subClassInternalName, int endIndex) {
		// object must support a (char[], offset, length) constructor, i.e. like string
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitTypeInsn(NEW, typeInternalName);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ILOAD, endIndex);
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitInsn(ISUB);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([CII)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, mappedClassInternalName, setterName, "(L" + typeInternalName + ";)V", false);
	}

}
