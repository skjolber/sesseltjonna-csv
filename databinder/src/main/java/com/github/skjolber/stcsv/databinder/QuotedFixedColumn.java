package com.github.skjolber.stcsv.databinder;

import static org.objectweb.asm.Opcodes.GOTO;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class QuotedFixedColumn extends QuotedColumn {

	protected int fixedSize;

	public QuotedFixedColumn(String name, int index, int quoteCharacter, int escapeCharacter, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces, int fixedSize) {
		super(name, index, quoteCharacter, escapeCharacter, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);

		this.fixedSize = fixedSize;
	}

	protected void inline(MethodVisitor mv, String subClassInternalName, int divider, int increment, boolean fillable) {
		
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

		writeValue(mv, subClassInternalName, true);

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

		// so in other words, fixed column size is not much of an optimization for quoted values;
		// the parser still needs to scan through all of the fixed column data if so.
		scanQuoted(mv, subClassInternalName, increment, divider, fillable, quoted, endLabel);
	}

}
