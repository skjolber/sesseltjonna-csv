package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPGT;
import static org.objectweb.asm.Opcodes.IF_ICMPLE;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.ISUB;
import static org.objectweb.asm.Opcodes.SIPUSH;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;


public class QuotedColumn extends AbstractColumn {

	protected final int quoteCharacter;
	protected final int escapeCharacter;
	
	public QuotedColumn(String name, int index, int quoteCharacter, int escapeCharacter, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
		
		this.quoteCharacter = quoteCharacter;
		this.escapeCharacter = escapeCharacter;
	}

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
		
		if(quoteCharacter == escapeCharacter) {
			// handle quoted
			mv.visitLabel(quoted);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "getCurrentRange", "()I", false);
			mv.visitVarInsn(ISTORE, rangeIndex);

			// start = currentOffset + 1
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitVarInsn(ISTORE, startIndex);
			
			
			// start quote while
			Label l19 = new Label();
			mv.visitLabel(l19);
			
			// scan to any char <= int value of quote character
			// tight inner loop
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, Integer.valueOf(quoteCharacter));
			mv.visitJumpInsn(IF_ICMPGT, l19);
			
			// check if quote or newline
			
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitLdcInsn(Integer.valueOf(quoteCharacter));
			Label l20 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l20);
			
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitInsn(CALOAD);
			mv.visitLdcInsn(Integer.valueOf(quoteCharacter));			
			Label l22 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l22);
			
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			Label l24 = new Label();
			mv.visitJumpInsn(IF_ICMPLE, l24);
			
			writeValue(mv, subClassInternalName);
	
			Label l26;
			if(optional) {
				l26 = l24;
			} else {
				l26 = new Label();			
			}
			mv.visitLabel(l26);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitLdcInsn(Integer.valueOf(divider));
			mv.visitJumpInsn(IF_ICMPNE, l26);
			mv.visitJumpInsn(GOTO, endLabel);
	
			if(!optional) {
				mv.visitLabel(l24);
				throwMappingException(mv);
			}			
			
			mv.visitLabel(l22);
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
			mv.visitJumpInsn(GOTO, l19);
			mv.visitLabel(l20);
			/*
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 10);
			mv.visitJumpInsn(IF_ICMPNE, l19);
			*/
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, rangeIndex);
			mv.visitJumpInsn(IF_ICMPNE, l19);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitInsn(ISUB);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);

			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "fill", "(I)I", false);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ISTORE, rangeIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);

			Label l34 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, l34);
	
			throwMappingException(mv);
			
			mv.visitLabel(l34);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, startIndex);
			
			// continue quote 
			mv.visitJumpInsn(GOTO, l19);
		} else {
			// handle quoted
			mv.visitLabel(quoted);
			
			// add tight inner loop for scanning
			// for a character higher than the maximum of escape and quote
			// if that is actually a character outside the most commonly used
			// characters (digits, a-z)
			
			boolean isLowAsciiCharacter = escapeCharacter < '0' && quoteCharacter < '0';

			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "getCurrentRange", "()I", false);
			mv.visitVarInsn(ISTORE, rangeIndex);
			
			if(isLowAsciiCharacter) {
				// start = currentOffset + 1
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitInsn(ICONST_1);
				mv.visitInsn(IADD);
				mv.visitVarInsn(ISTORE, startIndex);
			} else {
				// ++currentOffset;
				// start = currentOffset;
				mv.visitIincInsn(currentOffsetIndex, 1);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitVarInsn(ISTORE, startIndex);
			}
			
			Label l31 = new Label();
			mv.visitLabel(l31);
			
			Label afterCheckingForEscapeAndQuotes;
			if(isLowAsciiCharacter) {
				// scan to any char <= int value of max(quote character, escape character)
				// tight inner loop
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitIincInsn(currentOffsetIndex, 1);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitInsn(CALOAD);
				mv.visitIntInsn(BIPUSH, Integer.valueOf(Math.max(quoteCharacter, escapeCharacter)));
				mv.visitJumpInsn(IF_ICMPGT, l31);
				
				afterCheckingForEscapeAndQuotes = l31;
			} else {
				afterCheckingForEscapeAndQuotes = new Label();
			}
			
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitLdcInsn(Integer.valueOf(escapeCharacter));
			Label l32 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l32);
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
			mv.visitJumpInsn(GOTO, afterCheckingForEscapeAndQuotes);
			mv.visitLabel(l32);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(SIPUSH, quoteCharacter);
			Label l38 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l38);

			
			// got value, check if optional
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			Label l40 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, l40);
						
			Label l42 = new Label();
			if(optional) {
				mv.visitJumpInsn(GOTO, l42);
			} else {
				throwMappingException(mv);
			}
			
			mv.visitLabel(l40);
			
			writeValue(mv, subClassInternalName);

			mv.visitLabel(l42);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitLdcInsn(Integer.valueOf(divider));
			mv.visitJumpInsn(IF_ICMPNE, l42);
			
			Label l45 = new Label();
			mv.visitJumpInsn(GOTO, l45);
			mv.visitLabel(l38);
			
			/*
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 10);
			mv.visitJumpInsn(IF_ICMPNE, afterCheckingForEscapeAndQuotes);
			*/
			
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, rangeIndex);
			mv.visitJumpInsn(IF_ICMPNE, afterCheckingForEscapeAndQuotes);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitInsn(ISUB);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);

			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "fill", "(I)I", false);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ISTORE, rangeIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);

			Label l48 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, l48);
			
			throwMappingException(mv);

			mv.visitLabel(l48);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, startIndex);
			
			if(!isLowAsciiCharacter) {
				mv.visitLabel(afterCheckingForEscapeAndQuotes);
				mv.visitIincInsn(currentOffsetIndex, 1);
				mv.visitJumpInsn(GOTO, l31);
			}
			mv.visitLabel(l45);
			
		}
		mv.visitLabel(endLabel);
		
		mv.visitIincInsn(currentOffsetIndex, increment);
	}

	
}
