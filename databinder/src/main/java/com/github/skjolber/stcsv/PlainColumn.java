package com.github.skjolber.stcsv;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class PlainColumn extends AbstractColumn {

	public PlainColumn(String name, int index, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
	}

	protected void inline(MethodVisitor mv, String subClassInternalName, int divider, int increment) {

		Label skipLabel = null;
		if(optional) {
			skipLabel = ifAtChar(mv, divider);
		} else {
			Label valuePresent = ifNotAtChar(mv, divider);
			
			throwMappingException(mv);
			
			mv.visitLabel(valuePresent);
		}
		
		// save current offset
		saveOffsetInStart(mv);
		// do while loop
		doIncrementWhileNotEqualToDivider(mv, divider);		
		
		writeValue(mv, subClassInternalName);
		
		if(optional) {
			mv.visitLabel(skipLabel);
		}
		mv.visitIincInsn(currentOffsetIndex, increment);
	}
}
