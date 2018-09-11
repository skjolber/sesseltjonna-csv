package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;

import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;

public class ClassicPlainFixedColumn extends AbstractColumn {

	private int fixedSize;

	public ClassicPlainFixedColumn(String name, int index, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces, int fixedSize) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
		
		this.fixedSize = fixedSize;
	}

	public static class Middle {
		
		public static int orException(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target, char c) {
			
			if(current[currentOffset] == c) throw new CsvException();
			
			int start = currentOffset;
			
			currentOffset += length;
			
			// expect seperator character at the right location
			if(current[currentOffset] != c) throw new CsvException();
			
			consumer.consume(target, current, start, currentOffset);
			
			return ++currentOffset;
		}
		
		public static int orSkip(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target, char c) {
			
			if(current[currentOffset] == c) return ++currentOffset;
			
			int start = currentOffset;
			
			currentOffset += length;
			
			// expect seperator character at the right location
			if(current[currentOffset] != c) throw new CsvException();
			
			consumer.consume(target, current, start, currentOffset);
			
			return ++currentOffset;
		}
	}
	
	public static class Last {
		
		public static class NewLine {
			public static int orException(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target) {
				
				if(current[currentOffset] == '\n') throw new CsvException();
				
				int start = currentOffset;
				
				currentOffset += length;
				
				// expect newline character at the right location
				if(current[currentOffset] != '\n') throw new CsvException();
				
				consumer.consume(target, current, start, currentOffset);
				
				return ++currentOffset;
			}
			
			public static int orSkip(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target) {
				if(current[currentOffset] == '\n') return ++currentOffset;
				
				int start = currentOffset;
				
				currentOffset += length;
				
				// expect newline character at the right location
				if(current[currentOffset] != '\n') throw new CsvException();
				
				consumer.consume(target, current, start, currentOffset);
				
				return ++currentOffset;

			}
		}
		
		public static class NewLineCarriageReturn {
			public static int orException(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target) {
				
				// object id
				if(current[currentOffset] == '\r') throw new CsvException();
				
				int start = currentOffset;
				
				currentOffset += length;
				
				if(current[currentOffset] != '\r') throw new CsvException();
				
				consumer.consume(target, current, start, currentOffset);

				return currentOffset += 2; // skip carriage return and newline
			}
			
			public static int orSkip(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object object) {
				// shape_id
				if(current[currentOffset] == '\r') return currentOffset + 2; // skip carriage return and newline
				
				int start = currentOffset;
				
				currentOffset += length;
				
				if(current[currentOffset] != '\r') throw new CsvException();
				
				consumer.consume(object, current, start, currentOffset);

				return currentOffset += 2; // skip carriage return and newline
			}
		}
	}

	@Override
	public void middle(MethodVisitor mv, String subClassInternalName, boolean inline) {
		if(consumer == null) {
			throw new IllegalArgumentException();
		}
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitIntInsn(BIPUSH, fixedSize);
		mv.visitFieldInsn(GETSTATIC, subClassInternalName, "v" + index, "L" + consumerInternalName + ";");
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitIntInsn(BIPUSH, parent.getDivider());
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/csv/scan/PlainFixedColumn$Middle", optional ? "orSkip" : "orException", "([CIIL" + CsvMapper.consumerName + ";Ljava/lang/Object;C)I", false);
		mv.visitVarInsn(ISTORE, currentOffsetIndex);
	}

	@Override
	public void last(MethodVisitor mv, String subClassInternalName, boolean carriageReturn, boolean inline) {
		if(consumer == null) {
			throw new IllegalArgumentException();
		}
		String newLineType = carriageReturn ? "NewLineCarriageReturn" : "NewLine";
	
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitIntInsn(BIPUSH, fixedSize);
		mv.visitFieldInsn(GETSTATIC, subClassInternalName, "v" + index, "L" + consumerInternalName + ";");
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/csv/scan/PlainFixedColumn$Last$" + newLineType, optional ? "orSkip" : "orException", "([CIIL" + CsvMapper.consumerName + ";Ljava/lang/Object;)I", false);
		mv.visitVarInsn(ISTORE, currentOffsetIndex);		
	}	
}
