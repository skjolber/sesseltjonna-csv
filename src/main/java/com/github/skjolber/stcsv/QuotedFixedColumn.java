package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ATHROW;
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
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.ISUB;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.SIPUSH;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class QuotedFixedColumn extends AbstractColumn {

	protected int fixedSize;
	protected final int quoteCharacter;
	protected final int escapeCharacter;

	public QuotedFixedColumn(String name, int index, int quoteCharacter, int escapeCharacter, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces, int fixedSize) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);

		this.quoteCharacter = quoteCharacter;
		this.escapeCharacter = escapeCharacter;
		this.fixedSize = fixedSize;
	}

	protected void inline(MethodVisitor mv, String subClassInternalName, int divider, int increment) {
		
		/*

		if(current[currentOffset] != '"') {
			if(current[currentOffset] != ',') {
				currentOffset += fixedSize;
				
				// setter
			} else {
				throw new CsvMappingException();
			}
			currentOffset += n;
		} else {
		    currentOffset++;
		    
		    start = currentOffset;
			currentOffset += fixedSize;
			
			// setter
			
			currentOffset += n + 1;
		}
		
		*/
		
		// checks for empty value, but empty quotes are not supported
		
		Label quoted = ifAtChar(mv, quoteCharacter);
		Label emptyLabel = ifAtChar(mv, divider);
		
		saveOffsetInStart(mv);
		
		mv.visitIincInsn(currentOffsetIndex, fixedSize);

		writeValue(mv, subClassInternalName);

		if(optional) {
			mv.visitLabel(emptyLabel);
		}

		mv.visitIincInsn(currentOffsetIndex, increment);
		Label endLabel = new Label();
		mv.visitJumpInsn(GOTO, endLabel);

		if(!optional) {
			mv.visitLabel(emptyLabel);
			
			throwMappingException(mv);
		}

		if(quoteCharacter == escapeCharacter) {
			// handle quoted
			mv.visitLabel(quoted);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "getCurrentRange", "()I", false);
			mv.visitVarInsn(ISTORE, rangeIndex);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ISTORE, startIndex);
			Label l19 = new Label();
			mv.visitLabel(l19);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, quoteCharacter);
			Label l20 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l20);
			Label l21 = new Label();
			mv.visitLabel(l21);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, quoteCharacter);
			Label l22 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l22);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			Label l24 = new Label();
			mv.visitJumpInsn(IF_ICMPLE, l24);
			
			
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 4);
			mv.visitInsn(ISUB);
			mv.visitLdcInsn(new Integer(fixedSize));
			Label l46 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l46);
			
			throwMappingException(mv);
			
			mv.visitLabel(l46);
			
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
			mv.visitIntInsn(BIPUSH, divider);
			mv.visitJumpInsn(IF_ICMPNE, l26);
			Label l27 = new Label();
			mv.visitLabel(l27);
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
			Label l31 = new Label();
			mv.visitJumpInsn(GOTO, l31);
			mv.visitLabel(l20);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 10);
			mv.visitJumpInsn(IF_ICMPNE, l31);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, rangeIndex);
			mv.visitJumpInsn(IF_ICMPNE, l31);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitInsn(ISUB);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "fill", "(I)I", false);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ISTORE, rangeIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			Label l34 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, l34);
	
			throwMappingException(mv);
			
			mv.visitLabel(l34);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, startIndex);
			mv.visitLabel(l31);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitJumpInsn(GOTO, l19);
		} else {
			// handle quoted
			mv.visitLabel(quoted);

			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "getCurrentRange", "()I", false);
			mv.visitVarInsn(ISTORE, rangeIndex);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ISTORE, startIndex);
			Label l31 = new Label();
			mv.visitLabel(l31);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, escapeCharacter);
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
			Label l37 = new Label();
			mv.visitJumpInsn(GOTO, l37);
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
			
			
			/*
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(ISUB);
			mv.visitIntInsn(BIPUSH, fixedSize);
			Label l46 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l46);
			
			throwMappingException(mv);
			
			mv.visitLabel(l46);			
			*/
			
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 4);
			mv.visitInsn(ISUB);
			mv.visitLdcInsn(new Integer(fixedSize));
			Label l46 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l46);
			
			throwMappingException(mv);
			
			
			mv.visitLabel(l46);			
			
			
			writeValue(mv, subClassInternalName);

			mv.visitLabel(l42);
			mv.visitIincInsn(currentOffsetIndex, 1);
			Label l43 = new Label();
			mv.visitLabel(l43);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 44);
			mv.visitJumpInsn(IF_ICMPNE, l42);
			
			Label l45 = new Label();
			mv.visitJumpInsn(GOTO, l45);
			mv.visitLabel(l38);
			mv.visitVarInsn(ALOAD, currentArrayIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitInsn(CALOAD);
			mv.visitIntInsn(BIPUSH, 10);
			mv.visitJumpInsn(IF_ICMPNE, l37);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, rangeIndex);
			mv.visitJumpInsn(IF_ICMPNE, l37);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitVarInsn(ILOAD, startIndex);
			mv.visitInsn(ISUB);
			mv.visitVarInsn(ISTORE, currentOffsetIndex);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			mv.visitMethodInsn(INVOKEVIRTUAL, subClassInternalName, "fill", "(I)I", false);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ISTORE, rangeIndex);
			mv.visitVarInsn(ILOAD, currentOffsetIndex);
			Label l48 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, l48);
			
			throwMappingException(mv);

			mv.visitLabel(l48);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, startIndex);
			mv.visitLabel(l37);
			mv.visitIincInsn(currentOffsetIndex, 1);
			mv.visitJumpInsn(GOTO, l31);
			mv.visitLabel(l45);


		}
		mv.visitLabel(endLabel);
		
		mv.visitIincInsn(currentOffsetIndex, increment);		
		
		
		
		
		
		
		
		
		
		
		
	}

}
