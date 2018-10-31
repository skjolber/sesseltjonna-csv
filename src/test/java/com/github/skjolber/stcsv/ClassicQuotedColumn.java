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


public class ClassicQuotedColumn extends AbstractColumn {

	protected final int quoteCharacter;
	protected final int escapeCharacter;
	
	public ClassicQuotedColumn(String name, int index, int quoteCharacter, int escapeCharacter, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
		
		this.quoteCharacter = quoteCharacter;
		this.escapeCharacter = escapeCharacter;
	}

	public static class Middle {
		
		@SuppressWarnings("unchecked")
		public static int orException(AbstractCsvReader scanner, char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object target, char divider, char quoteCharacter, char escapeCharacter) throws IOException {
			if(current[currentOffset] != quoteCharacter) {
				return ClassicPlainColumn.Middle.orException(current, currentOffset, consumer, target, divider);
			}

			// now inside start quote
			int currentRange = scanner.getCurrentRange();
			int start = ++currentOffset;
			
			if(quoteCharacter == escapeCharacter) {
				do {
					if(current[currentOffset] == quoteCharacter) {
						if(current[currentOffset + 1] != quoteCharacter) {
							// 1x qoute
							break;
						}
	
						// 2x qoute
						// overwrite one of the quotes by copying the previous stuff forward
						// this approach assumes few quotes; is quick for a few qoutes but more expensive for many
						System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
						currentOffset++;
	
						start++;
					} else if(current[currentOffset] == '\n') {
						if(currentOffset == currentRange) {
							currentOffset = currentOffset - start; // what we've already read
	
							if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
								throw new IllegalArgumentException();
							}
							
							start = 0;
						} else {
							++currentOffset;
						}
					}
					currentOffset++;
				} while(true);
			} else {
				System.out.println("ADFSDF");

				do {
					if(current[currentOffset] == 456) {
						break;
					} else if(current[currentOffset] == 123) {
						// escaped value
						// overwrite the escape char by copying the previous stuff forward
						// this approach assumes few escapes; is quick for a few escapes but more expensive for many
						System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
						currentOffset++;
	
						start++;
					} else if(current[currentOffset] == '\n') {
						if(currentOffset == currentRange) {
							currentOffset = currentOffset - start; // what we've already read
	
							if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
								throw new IllegalArgumentException();
							}
							
							start = 0;
						} else {
							++currentOffset;
						}
					}
					currentOffset++;
				} while(true);

			}

			if(currentOffset > start) {
				consumer.consume(target, current, start, currentOffset);
			} else {
				throw new CsvException();
			}
			
			if(current[currentOffset] != divider) {
				while(current[++currentOffset] != divider);
			}
			
			return ++currentOffset;
		}

		@SuppressWarnings("unchecked")
		public static int orSkip(AbstractCsvReader scanner, char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object target, char divider, char quoteCharacter, char escapeCharacter) throws IOException {
			if(current[currentOffset] != quoteCharacter) {
				return ClassicPlainColumn.Middle.orSkip(current, currentOffset, consumer, target, divider);
			}
			
			// now inside start quote
			int currentRange = scanner.getCurrentRange();
			int start = ++currentOffset;
			
			if(quoteCharacter == escapeCharacter) {
	
				do {
					if(current[currentOffset] == quoteCharacter) {
						if(current[currentOffset + 1] != quoteCharacter) {
							// 1x qoute
							break;
						}
	
						// 2x qoute
						// overwrite one of the quotes by copying the previous stuff forward
						// this approach assumes few quotes; is quick for a few qoutes but more expensive for many
						System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
						currentOffset++;
	
						start++;
					} else if(current[currentOffset] == '\n') {
						if(currentOffset == currentRange) {
							currentOffset = currentOffset - start; // what we've already read
	
							if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
								throw new CsvException();
							}
							
							start = 0;
						} else {
							++currentOffset;
						}
					}
					currentOffset++;
				} while(true);
			} else {
				do {
					if(current[currentOffset] == quoteCharacter) {
						break;
					} else if(current[currentOffset] == quoteCharacter) {
						// escaped value
						// overwrite the escape char by copying the previous stuff forward
						// this approach assumes few escapes; is quick for a few escapes but more expensive for many
						System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
						currentOffset++;
	
						start++;
					} else if(current[currentOffset] == '\n') {
						if(currentOffset == currentRange) {
							currentOffset = currentOffset - start; // what we've already read
	
							if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
								throw new IllegalArgumentException();
							}
							
							start = 0;
						} else {
							++currentOffset;
						}
					}
					currentOffset++;
				} while(true);
			}
			
			if(currentOffset > start) {
				consumer.consume(target, current, start, currentOffset);
			}
			
			if(current[currentOffset] != divider) {
				while(current[++currentOffset] != divider);
			}
			
			return ++currentOffset;
		}

	}
	
	public static class Last {
		
		public static class NewLine {
			public static int orException(AbstractCsvReader scanner, char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object target, char quoteCharacter, char escapeCharacter) throws IOException {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainColumn.Last.NewLine.orException(current, currentOffset, consumer, target);
				}
				
				// now inside start quote
				int currentRange = scanner.getCurrentRange();
				int start = ++currentOffset;
				
				if(quoteCharacter == escapeCharacter) {
					do {
						if(current[currentOffset] == quoteCharacter) {
							if(current[currentOffset + 1] != quoteCharacter) {
								// 1x qoute
								break;
							}
	
							// 2x qoute
							// overwrite one of the quotes by copying the previous stuff forward
							// this approach assumes few quotes; is quick for a few qoutes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
							currentOffset++;
	
							start++;
						} else if(current[currentOffset] == '\n') {
							if(currentOffset == currentRange) {
								currentOffset = currentOffset - start; // what we've already read
	
								if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
									throw new IllegalArgumentException();
								}
								
								start = 0;
							} else {
								++currentOffset;
							}
						}
						currentOffset++;
					} while(true);
				} else {
					do {
						if(current[currentOffset] == quoteCharacter) {
							break;
						} else if(current[currentOffset] == quoteCharacter) {
							// escaped value
							// overwrite the escape char by copying the previous stuff forward
							// this approach assumes few escapes; is quick for a few escapes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
		
							currentOffset++;
		
							start++;
						} else if(current[currentOffset] == '\n') {
							if(currentOffset == currentRange) {
								currentOffset = currentOffset - start; // what we've already read
		
								if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
									throw new IllegalArgumentException();
								}
								
								start = 0;
							} else {
								++currentOffset;
							}
						}
						currentOffset++;
					} while(true);
				}

				if(currentOffset > start) {
					consumer.consume(target, current, start, currentOffset);
				} else {
					throw new CsvException();
				}
				
				if(current[currentOffset] != '\n') {
					while(current[++currentOffset] != '\n');
				}

				return ++currentOffset;					
			}
			
			public static int orSkip(AbstractCsvReader scanner, char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object target, char quoteCharacter, char escapeCharacter) throws IOException {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainColumn.Last.NewLine.orSkip(current, currentOffset, consumer, target);
				}
				
				// now inside start quote
				int currentRange = scanner.getCurrentRange();
				int start = ++currentOffset;
				
				if(quoteCharacter == escapeCharacter) {
					do {
						if(current[currentOffset] == quoteCharacter) {
							if(current[currentOffset + 1] != quoteCharacter) {
								// 1x qoute
								break;
							}
	
							// 2x qoute
							// overwrite one of the quotes by copying the previous stuff forward
							// this approach assumes few quotes; is quick for a few qoutes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
							currentOffset++;
	
							start++;
						} else if(current[currentOffset] == '\n') {
							if(currentOffset == currentRange) {
								currentOffset = currentOffset - start; // what we've already read
	
								if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
									throw new IllegalArgumentException();
								}
								
								start = 0;
							} else {
								++currentOffset;
							}
						}
						currentOffset++;
					} while(true);
				} else {
					do {
						if(current[currentOffset] == quoteCharacter) {
							break;
						} else if(current[currentOffset] == quoteCharacter) {
							// escaped value
							// overwrite the escape char by copying the previous stuff forward
							// this approach assumes few escapes; is quick for a few escapes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
		
							currentOffset++;
		
							start++;
						} else if(current[currentOffset] == '\n') {
							if(currentOffset == currentRange) {
								currentOffset = currentOffset - start; // what we've already read
		
								if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
									throw new IllegalArgumentException();
								}
								
								start = 0;
							} else {
								++currentOffset;
							}
						}
						currentOffset++;
					} while(true);
				}
				
				if(currentOffset > start) {
					consumer.consume(target, current, start, currentOffset);
				}
				
				if(current[currentOffset] != '\n') {
					while(current[++currentOffset] != '\n');
				}
				
				return ++currentOffset;			
			}
		}
		
		public static class NewLineCarriageReturn {
			public static int orException(AbstractCsvReader scanner, char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object target, char quoteCharacter, char escapeCharacter) throws IOException {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainColumn.Last.NewLineCarriageReturn.orException(current, currentOffset, consumer, target);
				}
				
				// now inside start quote
				int currentRange = scanner.getCurrentRange();
				int start = ++currentOffset;
				
				if(quoteCharacter == escapeCharacter) {
					do {
						if(current[currentOffset] == quoteCharacter) {
							if(current[currentOffset + 1] != quoteCharacter) {
								// 1x qoute
								break;
							}
	
							// 2x qoute
							// overwrite one of the quotes by copying the previous stuff forward
							// this approach assumes few quotes; is quick for a few qoutes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
							currentOffset++;
	
							start++;
						} else if(current[currentOffset] == '\n') {
							if(currentOffset == currentRange) {
								currentOffset = currentOffset - start; // what we've already read
	
								if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
									throw new CsvException();
								}
								
								start = 0;
							} else {
								++currentOffset;
							}
						}
						currentOffset++;
					} while(true);
				} else {
					do {
						if(current[currentOffset] == quoteCharacter) {
							break;
						} else if(current[currentOffset] == quoteCharacter) {
							// escaped value
							// overwrite the escape char by copying the previous stuff forward
							// this approach assumes few escapes; is quick for a few escapes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
		
							currentOffset++;
		
							start++;
						} else if(current[currentOffset] == '\n') {
							if(currentOffset == currentRange) {
								currentOffset = currentOffset - start; // what we've already read
		
								if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
									throw new IllegalArgumentException();
								}
								
								start = 0;
							} else {
								++currentOffset;
							}
						}
						currentOffset++;
					} while(true);
				}

				if(currentOffset > start) {
					consumer.consume(target, current, start, currentOffset);
				}
				
				if(current[currentOffset] != '\r') {
					while(current[++currentOffset] != '\r');
				}
				
				++currentOffset; // skip carriage return

				return ++currentOffset; // skip newline
			}
			
			public static int orSkip(AbstractCsvReader scanner, char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object target, char quoteCharacter, char escapeCharacter) throws IOException {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainColumn.Last.NewLineCarriageReturn.orSkip(current, currentOffset, consumer, target);
				}
				
				// now inside start quote
				int currentRange = scanner.getCurrentRange();
				int start = ++currentOffset;
				
				if(quoteCharacter == escapeCharacter) {
					do {
						if(current[currentOffset] == quoteCharacter) {
							if(current[currentOffset + 1] != quoteCharacter) {
								// 1x qoute
								break;
							}
	
							// 2x qoute
							// overwrite one of the quotes by copying the previous stuff forward
							// this approach assumes few quotes; is quick for a few qoutes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
							currentOffset++;
	
							start++;
						} else if(current[currentOffset] == '\n') {
							if(currentOffset == currentRange) {
								currentOffset = currentOffset - start; // what we've already read
	
								if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
									throw new IllegalArgumentException();
								}
								
								start = 0;
							} else {
								++currentOffset;
							}
						}
						currentOffset++;
					} while(true);
				} else {
					do {
						if(current[currentOffset] == quoteCharacter) {
							break;
						} else if(current[currentOffset] == quoteCharacter) {
							// escaped value
							// overwrite the escape char by copying the previous stuff forward
							// this approach assumes few escapes; is quick for a few escapes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
		
							currentOffset++;
		
							start++;
						} else if(current[currentOffset] == '\n') {
							if(currentOffset == currentRange) {
								currentOffset = currentOffset - start; // what we've already read
		
								if((currentRange = scanner.fill(currentOffset)) <= currentOffset) { // must get more bytes
									throw new IllegalArgumentException();
								}
								
								start = 0;
							} else {
								++currentOffset;
							}
						}
						currentOffset++;
					} while(true);
				}
				
				if(currentOffset > start) {
					consumer.consume(target, current, start, currentOffset);
				}
				
				if(current[currentOffset] != '\r') {
					while(current[++currentOffset] != '\r');
				}

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

		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, currentArrayIndex);
		mv.visitVarInsn(ILOAD, currentOffsetIndex);
		mv.visitFieldInsn(GETSTATIC, subClassInternalName, "v" + index, "L" + consumerInternalName + ";");
		mv.visitVarInsn(ALOAD, objectIndex);
		mv.visitIntInsn(BIPUSH, parent.getDivider());
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/csv/scan/QuotedColumn$Middle", optional ? "orSkip" : "orException", "(L" + CsvMapper.superClassInternalName + ";[CIL" + CsvMapper.consumerName + ";Ljava/lang/Object;C)I", false);
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
		mv.visitMethodInsn(INVOKESTATIC, "com/github/skjolber/csv/scan/QuotedColumn$Last$" + newLineType, optional ? "orSkip" : "orException", "(L" + CsvMapper.superClassInternalName + ";[CIL" + CsvMapper.consumerName + ";Ljava/lang/Object;)I", false);
		mv.visitVarInsn(ISTORE, currentOffsetIndex);		
	}
	
}
