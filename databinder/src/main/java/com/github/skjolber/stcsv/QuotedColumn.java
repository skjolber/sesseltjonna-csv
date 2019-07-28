package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.ALOAD;
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

	protected void inline(MethodVisitor mv, String subClassInternalName, int divider, int increment, boolean fillable) {

		Label quoted = ifAtChar(mv, quoteCharacter); // quoted
		Label plainEmpty = ifAtChar(mv, divider); // empty
	
		saveOffsetInStart(mv);
		
		doIncrementWhileNotEqualToDivider(mv, divider);
		
		writeValue(mv, subClassInternalName, true);
		
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
			
			// rangeIndex = this.getCurrentRange();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "getCurrentRange", "()I", false);
			mv.visitVarInsn(ISTORE, rangeIndex);

			// start = currentOffset + 1
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitVarInsn(ISTORE, startIndex);
			
			
			// start quote while
			Label whileQuoted = new Label();
			mv.visitLabel(whileQuoted);
			
			// scan to any char <= int value of quote character
			// tight inner loop
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, Integer.valueOf(quoteCharacter));
			mv.visitJumpInsn(IF_ICMPGT, whileQuoted);
			
			// check if quote or newline
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitLdcInsn(Integer.valueOf(quoteCharacter));
			
			Label nonQuoteCharacter = new Label();
			mv.visitJumpInsn(IF_ICMPNE, nonQuoteCharacter);
			
			// fall through if quote character

			// currentOffset++;
			mv.visitIincInsn(currentOffsetIndex, 1);

			Label safeToReadNextCharacter = new Label();
			fill(mv, subClassInternalName, safeToReadNextCharacter, fillable);
			
			// safe to read next character from buffer
			mv.visitLabel(safeToReadNextCharacter);
			
			// if (current[currentOffset] != '"') {
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, quoteCharacter);
			

			Label isDoubleQuote = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, isDoubleQuote);

			
			// fall through; single quote
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(ISUB);
			mv.visitVarInsn(ILOAD, startIndex);			
			Label l24 = new Label();
			mv.visitJumpInsn(IF_ICMPLE, l24);
			
			writeValue(mv, subClassInternalName, false);
	
			Label l26;
			if(optional) {
				l26 = l24;
			} else {
				l26 = new Label();			
			}
			mv.visitLabel(l26);
			

			// while (current[currentOffset] != ',') {
			//   ++currentOffset;
			// }
			// break;
			
			{
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitInsn(CALOAD);
				mv.visitLdcInsn(Integer.valueOf(divider));
				mv.visitJumpInsn(IF_ICMPEQ, endLabel);
				{
					mv.visitIincInsn(currentOffsetIndex, 1);
					mv.visitJumpInsn(GOTO, l26);
				}
			}
			
			if(!optional) {
				mv.visitLabel(l24);
				throwMappingException(mv);
			}			
			
			mv.visitLabel(isDoubleQuote);
			
			// System.arraycopy(current, start, current, ++start, currentOffset - start);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ILOAD, 4);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitIincInsn(4, 1);
			mv.visitVarInsn(ILOAD, 4);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 4);
			mv.visitInsn(ISUB);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false);
			mv.visitJumpInsn(GOTO, whileQuoted);
			
			// NON-QUOTED character
			mv.visitLabel(nonQuoteCharacter);
			/*
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 10);
			mv.visitJumpInsn(IF_ICMPNE, l19);
			*/
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, rangeIndex);
			mv.visitJumpInsn(IF_ICMPNE, whileQuoted);
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
			mv.visitJumpInsn(GOTO, whileQuoted);
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
			
			Label whileQuoted = new Label();
			mv.visitLabel(whileQuoted);
			
			Label incrementCurrentOffsetWhileQuoted;
			if(isLowAsciiCharacter) {
				// scan to any char <= int value of max(quote character, escape character)
				// tight inner loop
				mv.visitVarInsn(ALOAD, currentArrayIndex);
				mv.visitIincInsn(currentOffsetIndex, 1);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitInsn(CALOAD);
				mv.visitIntInsn(BIPUSH, Integer.valueOf(Math.max(quoteCharacter, escapeCharacter)));
				mv.visitJumpInsn(IF_ICMPGT, whileQuoted);
				
				incrementCurrentOffsetWhileQuoted = whileQuoted;
			} else {
				incrementCurrentOffsetWhileQuoted = new Label();
			}
			
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitLdcInsn(Integer.valueOf(escapeCharacter));
			Label l32 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l32);
			
			
			
			
			// System.arraycopy(current, start++, current, start, ++currentOffset - start);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitIincInsn(startIndex, 1);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitInsn(ISUB);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false);


			
			Label l38 = new Label();
			mv.visitJumpInsn(GOTO, l38);
			
			
			mv.visitLabel(l32);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(SIPUSH, quoteCharacter);
			
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
			
			writeValue(mv, subClassInternalName, true);

			mv.visitLabel(l42);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitLdcInsn(Integer.valueOf(divider));
			mv.visitJumpInsn(IF_ICMPNE, l42);
			
			mv.visitJumpInsn(GOTO, endLabel);
			
			mv.visitLabel(l38);
			
			fill(mv, subClassInternalName, incrementCurrentOffsetWhileQuoted, fillable);
			
			if(!isLowAsciiCharacter) {
				mv.visitLabel(incrementCurrentOffsetWhileQuoted);
				mv.visitIincInsn(currentOffsetIndex, 1);
			}
			mv.visitJumpInsn(GOTO, whileQuoted);
		}
		mv.visitLabel(endLabel);
		
		mv.visitIincInsn(currentOffsetIndex, increment);
	}

	private void fill(MethodVisitor mv, String subClassInternalName, Label safeToReadNextCharacter, boolean fillable) {
		{

			// if (currentOffset == rangeIndex) {
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, rangeIndex);
			mv.visitJumpInsn(IF_ICMPNE, safeToReadNextCharacter);

			// fall through if currentOffset is at rangeIndex

			// currentOffset -= start;
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitInsn(ISUB);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
			
			if(fillable) {
				// reading more characters from underlying stream must be possible (EOF not acceptable)
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
				
				Label l33 = new Label();
				mv.visitJumpInsn(IF_ICMPGT, l33);
				
				throwMappingException(mv);
				mv.visitLabel(l33);
				
			} else {
				// reading more characters is potentially not possible (EOF acceptable)
				// rangeIndex = this.fill(currentOffset + 1);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ILOAD, currentOffsetIndex);
				mv.visitInsn(ICONST_1);
				mv.visitInsn(IADD);
				mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "fill", "(I)I", false);
				mv.visitVarInsn(ISTORE, rangeIndex);
			}

			// start = 0
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, startIndex);
		}
	}

	
}
