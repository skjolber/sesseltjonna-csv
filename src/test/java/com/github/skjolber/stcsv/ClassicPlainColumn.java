package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;

import java.io.IOException;

import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;

public class ClassicPlainColumn extends AbstractColumn {

	public ClassicPlainColumn(String name, int index, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
	}

	public static int skipEmptyLines(AbstractCsvReader scanner, char[] current, int currentOffset) throws IOException {
		// skip empty lines
		if(current[currentOffset] == '\n') {
			int currentRange = scanner.getCurrentRange();
			do {
				if(currentOffset == currentRange) {
					if((currentRange = scanner.fill()) > 0) {
						return -1;
					}
					currentOffset = 0;
				} else {
					++currentOffset;
				}
				
				if(current[currentOffset] != '\r' && current[currentOffset] != '\n') {
					break;
				}
			} while(true);
		}		
		return currentOffset;
	}	

	public static class Middle {
		
		public static int orException(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object, char c) {
			
			// object id
			if(current[currentOffset] == c) throw new CsvException();
			
			int start = currentOffset;
			while(current[++currentOffset] != c);
			
			consumer.consume(object, current, start, currentOffset);
			
			return ++currentOffset;
		}
		
		public static int orSkip(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object, char c) {
			// shape_id
			if(current[currentOffset] == c) return ++currentOffset;
			
			int start = currentOffset;
			while(current[++currentOffset] != c);
			
			consumer.consume(object, current, start, currentOffset);

			return ++currentOffset;
		}
		
	}

	public static class Last {
		
		public static class NewLine {
			public static int orException(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object) {
				
				// object id
				if(current[currentOffset] == '\n') throw new CsvException();
				
				int start = currentOffset;
				while(current[++currentOffset] != '\n');
				
				consumer.consume(object, current, start, currentOffset);
				
				return ++currentOffset;
			}
			
			public static int orSkip(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object) {
				// shape_id
				if(current[currentOffset] == '\n') return ++currentOffset;
				
				int start = currentOffset;
				while(current[++currentOffset] != '\n');
				
				if(currentOffset > start) {
					consumer.consume(object, current, start, currentOffset);
				}
	
				return ++currentOffset;
			}
		}
		
		public static class NewLineCarriageReturn {
			public static int orException(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object) {
				
				// object id
				if(current[currentOffset] == '\r') throw new CsvException();
				
				int start = currentOffset;
				while(current[++currentOffset] != '\r');
				
				consumer.consume(object, current, start, currentOffset);

				++currentOffset; // skip carriage return

				return ++currentOffset; // skip newline
			}
			
			public static int orSkip(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object) {
				// shape_id
				if(current[currentOffset] == '\r') return currentOffset + 2; // skip carriage return and newline
				
				int start = currentOffset;
				while(current[++currentOffset] != '\r');
				
				consumer.consume(object, current, start, currentOffset);

				++currentOffset; // skip carriage return

				return ++currentOffset; // skip newline
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
		mv.visitFieldInsn(GETSTATIC, subClassInternalName, "v" + index, "L" + consumerInternalName + ";");
		mv.visitVarInsn(ALOAD, objectIndex);

		mv.visitIntInsn(BIPUSH, parent.getDivider());
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/csv/scan/PlainColumn$Middle", optional ? "orSkip" : "orException", "([CIL" + CsvMapper.consumerName + ";Ljava/lang/Object;C)I", false);
		mv.visitVarInsn(ISTORE, currentOffsetIndex);
	}

	@Override
	public void last(MethodVisitor mv, String subClassInternalName, boolean carriageReturn, boolean inline) {
		if(consumer == null) {
			throw new IllegalArgumentException();
		}
		String newLineType = carriageReturn ? "NewLineCarriageReturn" : "NewLine";
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitFieldInsn(GETSTATIC, subClassInternalName, "v" + index, "L" + consumerInternalName + ";");
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/csv/scan/PlainColumn$Last$" + newLineType, optional ? "orSkip" : "orException", "([CIL" + CsvMapper.consumerName + ";Ljava/lang/Object;)I", false);
		mv.visitVarInsn(ISTORE, currentOffsetIndex);		
	}
	
}
