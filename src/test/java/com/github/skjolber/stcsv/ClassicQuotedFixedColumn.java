package com.github.skjolber.stcsv;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;

public class ClassicQuotedFixedColumn extends AbstractColumn {

	private int fixedSize;
	protected final int quoteCharacter;
	protected final int escapeCharacter;

	public ClassicQuotedFixedColumn(String name, int index, int quoteCharacter, int escapeCharacter, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces, int fixedSize) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);

		this.quoteCharacter = quoteCharacter;
		this.escapeCharacter = escapeCharacter;
		
		this.fixedSize = fixedSize;
	}

	public static class Middle {

		public static int orException(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target, char divider, char quoteCharacter, char escapeCharacter) {
			if(current[currentOffset] != quoteCharacter) {
				return ClassicPlainFixedColumn.Middle.orException(current, currentOffset, length, consumer, target, divider);
			}

			// skip quote
			currentOffset++;

			int start = currentOffset;

			currentOffset += length;

			// expect quote character at the end location
			if(current[currentOffset] != quoteCharacter) throw new CsvException();

			consumer.consume(target, current, start, currentOffset);

			return currentOffset += 2;
		}

		public static int orSkip(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target, char divider, char quoteCharacter, char escapeCharacter) {
			if(current[currentOffset] != quoteCharacter) {
				return ClassicPlainFixedColumn.Middle.orSkip(current, currentOffset, length, consumer, target, divider);
			}

			// skip quote
			if(current[++currentOffset] == quoteCharacter) {
				return currentOffset += 2;
			}

			int start = currentOffset;

			currentOffset += length;

			// expect quote character at the end location
			if(current[currentOffset] != quoteCharacter) throw new CsvException();

			consumer.consume(target, current, start, currentOffset);

			return currentOffset += 2;
		}
	}

	public static class Last {

		public static class NewLine {
			public static int orException(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target, char quoteCharacter, char escapeCharacter) {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainFixedColumn.Last.NewLine.orException(current, currentOffset, length, consumer, target);
				}

				// skip quote
				currentOffset++;

				int start = currentOffset;

				currentOffset += length;

				// expect quote character at the end location
				if(current[currentOffset] != quoteCharacter) throw new CsvException();

				consumer.consume(target, current, start, currentOffset);

				return currentOffset += 2;
			}

			public static int orSkip(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target, char quoteCharacter, char escapeCharacter) {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainFixedColumn.Last.NewLine.orSkip(current, currentOffset, length, consumer, target);
				}

				// skip quote
				if(current[++currentOffset] == quoteCharacter) {
					return currentOffset += 2;
				}

				int start = currentOffset;

				currentOffset += length;

				// expect quote character at the end location
				if(current[currentOffset] != quoteCharacter) throw new CsvException();

				consumer.consume(target, current, start, currentOffset);

				return currentOffset += 2;

			}
		}

		public static class NewLineCarriageReturn {
			public static int orException(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target, char quoteCharacter, char escapeCharacter) {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainFixedColumn.Last.NewLineCarriageReturn.orException(current, currentOffset, length, consumer, target);
				}

				// skip quote
				currentOffset++;

				int start = currentOffset;

				currentOffset += length;

				// expect quote character at the end location
				if(current[currentOffset] != quoteCharacter) throw new CsvException();

				consumer.consume(target, current, start, currentOffset);

				return currentOffset += 3;
			}

			public static int orSkip(char[] current, int currentOffset, int length, CsvColumnValueConsumer consumer, Object target, char quoteCharacter, char escapeCharacter) {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainFixedColumn.Last.NewLineCarriageReturn.orSkip(current, currentOffset, length, consumer, target);
				}

				// skip quote
				if(current[++currentOffset] == quoteCharacter) {
					return currentOffset += 3;
				}

				int start = currentOffset;

				currentOffset += length;

				// expect quote character at the end location
				if(current[currentOffset] != quoteCharacter) throw new CsvException();

				consumer.consume(target, current, start, currentOffset);

				return currentOffset += 3;
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
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/csv/scan/QuotedFixedColumn$Middle", optional ? "orSkip" : "orException", "([CIIL" + CsvMapper.consumerName + ";Ljava/lang/Object;C)I", false);
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
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/csv/scan/QuotedFixedColumn$Last$" + newLineType, optional ? "orSkip" : "orException", "([CIIL" + CsvMapper.consumerName + ";Ljava/lang/Object;)I", false);
		mv.visitVarInsn(ISTORE, currentOffsetIndex);		
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
			currentOffset += fixedSize + 1;
			
			// setter
			
			currentOffset += n + 1;
		}

		System.out.println("AFTER");
		
		*/
		
		// checks for empty value, but empty quotes are not supported
		
		Label quoteLabel = ifAtChar(mv, quoteCharacter);
		Label emptyLabel = ifAtChar(mv, divider);
		
		mv.visitIincInsn(1, fixedSize);

		writeValue(mv, subClassInternalName);

		if(optional) {
			mv.visitLabel(emptyLabel);
		}

		mv.visitIincInsn(1, increment);
		Label endLabel = new Label();
		mv.visitJumpInsn(GOTO, endLabel);

		if(!optional) {
			mv.visitLabel(emptyLabel);
			
			throwMappingException(mv);
		}
		
		mv.visitLabel(quoteLabel);
		mv.visitIincInsn(1, fixedSize + 1); // skip quote + fixed size
		
		writeValue(mv, subClassInternalName);
		
		mv.visitIincInsn(1, increment + 1); // end quote + dividers
		mv.visitLabel(endLabel);
	}

}
