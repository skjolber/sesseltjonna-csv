package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CALOAD;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IF_ICMPGT;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.ISUB;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class NoLineBreakQuotedColumn extends AbstractColumn {

	protected final int quoteCharacter;
	protected final int escapeCharacter;

	public NoLineBreakQuotedColumn(String name, int index, int quoteCharacter, int escapeCharacter, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
		
		this.quoteCharacter = quoteCharacter;
		this.escapeCharacter = escapeCharacter;
	}

	@Override
	protected void inline(MethodVisitor mv, String subClassInternalName, int divider, int increment) {

		Label quoted = ifAtChar(mv, quoteCharacter); // quoted
		Label plainEmpty = ifAtChar(mv, divider); // empty
	
		saveOffsetInStart(mv);
		
		doIncrementWhileNotEqualToDivider(mv, divider);
		
		writeValue(mv, subClassInternalName);
		
		Label endLabel;
		if(optional) {
			endLabel = plainEmpty;
		} else {
			endLabel = new Label();
		}
		
		mv.visitJumpInsn(GOTO, endLabel);
		
		if(!optional) {
			// handle plain empty value
			mv.visitLabel(plainEmpty);
			throwMappingException(mv);
		}		

		mv.visitLabel(quoted);
		mv.visitIincInsn(currentOffsetIndex, 1);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitVarInsn(ISTORE, startIndex);

		// handle quotes
		if(quoteCharacter == escapeCharacter) {
			
			
			Label l26 = new Label();
			mv.visitJumpInsn(GOTO, l26);
			Label l27 = new Label();
			mv.visitLabel(l27);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitLabel(l26);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, quoteCharacter);
			mv.visitJumpInsn(IF_ICMPNE, l27);
			
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, quoteCharacter);
			Label l29 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l29);
			
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitInsn(ISUB);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false);
			mv.visitIincInsn(currentOffsetIndex, 2);
			mv.visitIincInsn(4, currentOffsetIndex);
			Label l34 = new Label();
			mv.visitJumpInsn(GOTO, l34);
			mv.visitLabel(l29);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			Label nonEmptyValue = new Label();
			mv.visitJumpInsn(IF_ICMPGT, nonEmptyValue);
	
			Label skipToComma = new Label();
			
			if(!optional) {
				throwMappingException(mv);
			} else {
				mv.visitJumpInsn(GOTO, skipToComma);
			}
			
			mv.visitLabel(nonEmptyValue);
			
			writeValue(mv, subClassInternalName);

			mv.visitLabel(skipToComma);
			
			doIncrementWhileNotEqualToDivider(mv, divider);
			
			mv.visitJumpInsn(GOTO, endLabel);
			mv.visitLabel(l34);
			mv.visitJumpInsn(GOTO, l26);
		} else {
			Label l30 = new Label();
			mv.visitLabel(l30);
			
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, escapeCharacter);
			Label l31 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l31);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitInsn(ISUB);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitIincInsn(startIndex, 1);
			Label l36 = new Label();
			mv.visitJumpInsn(GOTO, l36);
			mv.visitLabel(l31);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, quoteCharacter);
			mv.visitJumpInsn(IF_ICMPNE, l36);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			Label nonEmptyValue = new Label();
			mv.visitJumpInsn(IF_ICMPGT, nonEmptyValue);

			Label skipToComma = new Label();
			
			if(!optional) {
				throwMappingException(mv);
			} else {
				mv.visitJumpInsn(GOTO, skipToComma);
			}
			
			mv.visitLabel(nonEmptyValue);
			
			writeValue(mv, subClassInternalName);

			mv.visitLabel(skipToComma);
			
			doIncrementWhileNotEqualToDivider(mv, divider);
			
			mv.visitJumpInsn(GOTO, endLabel);
			mv.visitLabel(l36);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitJumpInsn(GOTO, l30);
		}
		mv.visitLabel(endLabel);
		
		mv.visitIincInsn(currentOffsetIndex, increment);		
	}
	
}
