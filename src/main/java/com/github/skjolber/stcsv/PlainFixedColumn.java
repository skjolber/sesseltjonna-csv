package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.GOTO;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class PlainFixedColumn extends AbstractColumn {

	private int fixedSize;

	public PlainFixedColumn(String name, int index, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces, int fixedSize) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
		
		this.fixedSize = fixedSize;
	}

	protected void inline(MethodVisitor mv, String subClassInternalName, int divider, int increment) {
		Label emptyLabel = ifAtChar(mv, divider);
		
		saveOffsetInStart(mv);
		
		// add fixed size
		mv.visitIincInsn(currentOffsetIndex, fixedSize);

		writeValue(mv, subClassInternalName);
		
		if(!optional) {
			Label skip = new Label();
			mv.visitJumpInsn(GOTO, skip);

			mv.visitLabel(emptyLabel);
			throwMappingException(mv);
			
			mv.visitLabel(skip);
		} else {
			mv.visitLabel(emptyLabel);
		}
		
		mv.visitIincInsn(currentOffsetIndex, increment); // skip divider
	}
}
