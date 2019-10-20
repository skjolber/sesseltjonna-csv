package com.github.skjolber.stcsv.databinder;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.databinder.projection.BiConsumerProjection;
import com.github.skjolber.stcsv.databinder.projection.TriConsumerProjection;
import com.github.skjolber.stcsv.databinder.projection.ValueProjection;

public abstract class AbstractColumn {

	protected final String name;
	protected final int index;
	protected final boolean optional;
	
	protected final boolean trimTrailingWhitespaces;
	protected final boolean trimLeadingWhitespaces;

	protected AbstractCsvMapper<?> parent;

	protected final static int currentOffsetIndex = AbstractCsvMapper.VAR_CURRENT_OFFSET;
	protected final static int currentArrayIndex = AbstractCsvMapper.VAR_CURRENT_ARRAY;
	protected final static int objectIndex = AbstractCsvMapper.VAR_OBJECT;
	protected final static int startIndex = AbstractCsvMapper.VAR_START;
	protected final static int rangeIndex = AbstractCsvMapper.VAR_RANGE;
	protected final static int intermediateIndex = AbstractCsvMapper.VAR_INTERMEDIATE_OBJECT;
	
	protected Class<?> intermediate;
	protected ValueProjection projection;

	public AbstractColumn(String name, int index, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces) {
		this.name = name;
		this.index = index;
		this.optional = optional;
		this.trimTrailingWhitespaces = trimTrailingWhitespaces;
		this.trimLeadingWhitespaces = trimLeadingWhitespaces;
	}

	public void middle(MethodVisitor mv, String subClassInternalName, boolean inline) {
		inline(mv, subClassInternalName, parent.getDivider(), 1, true);
	}

	public void last(MethodVisitor mv, String subClassInternalName, boolean carriageReturn, boolean inline) {
		if(carriageReturn) {
			inline(mv, subClassInternalName, '\r', 2, false); // \r\n
		} else {
			inline(mv, subClassInternalName, '\n', 1, false); // \n
		}
	}
	
	/**
	 * Inline column handling
	 * 
	 * @param mv method visitor
	 * @param subClassInternalName target parser class
	 * @param divider divider character
	 * @param increment increment after last character
	 * @param fillable if true then a fill operation must be successful
	 */
	
	protected abstract void inline(MethodVisitor mv, String subClassInternalName, int divider, int increment, boolean fillable);
	
	public String getName() {
		return name;
	}

	public void setParent(AbstractCsvMapper<?> parent) {
		this.parent = parent;
	}

	public int getIndex() {
		return index;
	}
	
	public boolean isBiConsumer() {
		return projection instanceof BiConsumerProjection;
	}

	public boolean isTriConsumer() {
		return projection instanceof TriConsumerProjection;
	}
	
	
	protected Label ifNotAtChar(MethodVisitor mv, int character) {
		// jump to label if the two integer refs are not equal
		return jumpToLabelForChar(mv, character, IF_ICMPNE);
	}

	protected Label ifAtChar(MethodVisitor mv, int character) {
		// jump to label if the two integer refs are equal
		return jumpToLabelForChar(mv, character, IF_ICMPEQ);
	}

	protected Label jumpToLabelForChar(MethodVisitor mv, int character, int code) {
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(CALOAD);
		mv.visitLdcInsn(Integer.valueOf(character));
		Label l27 = new Label();
		mv.visitJumpInsn(code, l27);
		return l27;
	}

	protected void throwMappingException(MethodVisitor mv) {
		mv.visitTypeInsn(NEW, "com/github/skjolber/stcsv/CsvException");
		mv.visitInsn(DUP);
		mv.visitLdcInsn("Illegal value in field '" + name + "'");
		mv.visitMethodInsn(INVOKESPECIAL, "com/github/skjolber/stcsv/CsvException", "<init>", "(Ljava/lang/String;)V", false);
		mv.visitInsn(ATHROW);
	}

	protected void doIncrementWhileNotEqualToDivider(MethodVisitor mv, int divider) {
		Label l29 = new Label();
		mv.visitLabel(l29);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitIincInsn(currentOffsetIndex, 1);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitInsn(CALOAD);
		mv.visitLdcInsn(Integer.valueOf(divider));
		mv.visitJumpInsn(IF_ICMPNE, l29);
	}
	
	
	
	
	
	protected void doDecrementWhileAtWhitespace(MethodVisitor mv, int variableIndex) {
		
		// while (current[--currentOffset] == ' ' || current[currentOffset] == '\t');
		Label condition = new Label();
		mv.visitLabel(condition);
		
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitIincInsn(variableIndex, -1);
		mv.visitVarInsn(ILOAD, variableIndex);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, 32);
		mv.visitJumpInsn(IF_ICMPEQ, condition);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ILOAD, variableIndex);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, 9);
		mv.visitJumpInsn(IF_ICMPEQ, condition);
	}
	
	protected void trimTrailingWhitespace(MethodVisitor mv, boolean inclusive) {
		// keep the 'end' index in the range index
		saveOffsetInRange(mv);
		
		if(!inclusive) {
			// decrement range
			mv.visitIincInsn(rangeIndex, -1);
		}
		
		doDecrementWhileAtWhitespace(mv, rangeIndex);
		
		// increment one so that the end index is non-inclusive
		mv.visitIincInsn(rangeIndex, 1);
	}	

	protected void saveOffsetInStart(MethodVisitor mv) {
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitVarInsn(ISTORE, startIndex);
	}

	protected void saveOffsetInRange(MethodVisitor mv) {
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitVarInsn(ISTORE, rangeIndex);
	}
	
	protected void writeValue(MethodVisitor mv, String subClassInternalName, boolean inclusive) {
		int endIndex;
		
		if(trimLeadingWhitespaces) {
			trimWhitespace(mv, subClassInternalName, startIndex);
		}

		if(trimTrailingWhitespaces) {
			trimTrailingWhitespace(mv, inclusive);
			endIndex = rangeIndex;
		} else if(!inclusive) {
			saveOffsetInRange(mv);
			
			// decrement range by one
			mv.visitIincInsn(rangeIndex, -1);
			
			endIndex = rangeIndex;
		} else {
			endIndex = currentOffsetIndex;
		}
		
		Label emptyValueLabel = null;
		if(trimLeadingWhitespaces || trimTrailingWhitespaces) {
			// check whether the trimming reduced the content to empty string
			Label writeValueLabel = ifLargerThanStart(mv, endIndex);
			
			if(optional) {
				emptyValueLabel = new Label();
				mv.visitJumpInsn(GOTO, emptyValueLabel);
			} else {
				throwMappingException(mv);
			}
			
			mv.visitLabel(writeValueLabel);
		}
		
		projection.write(mv, subClassInternalName, endIndex);
		
		if(emptyValueLabel != null) {
			mv.visitLabel(emptyValueLabel);
		}
			
	}

	protected Label ifLargerThanStart(MethodVisitor mv, int endIndex) {
		// if(endIndex > start) {
		mv.visitVarInsn(ILOAD, startIndex);
		mv.visitVarInsn(ILOAD, endIndex);
		Label l24 = new Label();
		mv.visitJumpInsn(IF_ICMPLT, l24);
		
		return l24;
	}

	protected void trimWhitespace(MethodVisitor mv, String subClassInternalName, int index) {
		
		Label condition = new Label();
		mv.visitJumpInsn(GOTO, condition);
		
		Label add = new Label();
		mv.visitLabel(add);
		mv.visitIincInsn(index, 1);
		mv.visitLabel(condition);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, index);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, 32);
		mv.visitJumpInsn(IF_ICMPEQ, add);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, index);
		mv.visitInsn(CALOAD);
		mv.visitIntInsn(BIPUSH, 9);
		mv.visitJumpInsn(IF_ICMPEQ, add);
	}

	public ValueProjection getProjection() {
		return projection;
	}
	
	public void setProjection(ValueProjection projection) {
		this.projection = projection;
	}
}
