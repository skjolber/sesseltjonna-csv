package com.github.skjolber.stcsv;

import java.io.IOException;

import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;

public class ClassicNoLineBreakQuotedColumn extends AbstractColumn {

	public ClassicNoLineBreakQuotedColumn(String name, int index, boolean optional, boolean trimTrailingWhitespaces, boolean trimLeadingWhitespaces) {
		super(name, index, optional, trimTrailingWhitespaces, trimLeadingWhitespaces);
	}

	public static class Middle {
		
		@SuppressWarnings("unchecked")
		public static int orException(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object, char divider, char quoteCharacter, char escapeCharacter) throws IOException {
			if(current[currentOffset] != quoteCharacter) {
				return ClassicPlainColumn.Middle.orException(current, currentOffset, consumer, object, divider);
			}

			// now inside start quote
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
						// this approach assumes few quotes; is quick for a few quotes but more expensive for many
						System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
						currentOffset++;
	
						start++;
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
					}
					currentOffset++;
				} while(true);
			}
			
			if(currentOffset > start) {
				consumer.consume(object, current, start, currentOffset);
			} else {
				throw new CsvException();
			}
			
			if(current[currentOffset] != divider) {
				while(current[++currentOffset] != divider);
			}
			
			return ++currentOffset;
		}

		@SuppressWarnings("unchecked")
		public static int orSkip(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object, char divider, char quoteCharacter, char escapeCharacter) throws IOException {
			if(current[currentOffset] != quoteCharacter) {
				return ClassicPlainColumn.Middle.orSkip(current, currentOffset, consumer, object, divider);
			}
			
			// now inside start quote
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
						// this approach assumes few quotes; is quick for a few quotes but more expensive for many
						System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
						currentOffset++;
	
						start++;
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
					}
					currentOffset++;
				} while(true);
			}
			
			if(currentOffset > start) {
				consumer.consume(object, current, start, currentOffset);
			}
			
			if(current[currentOffset] != divider) {
				while(current[++currentOffset] != divider);
			}
			
			return ++currentOffset;
		}

	}
	
	public static class Last {
		
		public static class NewLine {
			public static int orException(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object, char quoteCharacter, char escapeCharacter) throws IOException {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainColumn.Last.NewLine.orException(current, currentOffset, consumer, object);
				}
				
				// now inside start quote
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
							// this approach assumes few quotes; is quick for a few quotes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
							currentOffset++;
	
							start++;
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
						}
						currentOffset++;
					} while(true);
				}
				
				if(currentOffset > start) {
					consumer.consume(object, current, start, currentOffset);
				} else {
					throw new CsvException();
				}
				
				if(current[currentOffset] != '\n') {
					while(current[++currentOffset] != '\n');
				}
				
				return ++currentOffset;					
			}
			
			public static int orSkip(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object, char quoteCharacter, char escapeCharacter) throws IOException {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainColumn.Last.NewLine.orSkip(current, currentOffset, consumer, object);
				}
				
				// now inside start quote
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
							// this approach assumes few quotes; is quick for a few quotes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
							currentOffset++;
	
							start++;
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
						}
						currentOffset++;
					} while(true);
				}
				
				if(currentOffset > start) {
					consumer.consume(object, current, start, currentOffset);
				}
				
				if(current[currentOffset] != '\n') {
					while(current[++currentOffset] != '\n');
				}
				
				return ++currentOffset;			
			}
		}
		
		public static class NewLineCarriageReturn {
			public static int orException(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object, char quoteCharacter, char escapeCharacter) throws IOException {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainColumn.Last.NewLineCarriageReturn.orException(current, currentOffset, consumer, object);
				}
				
				// now inside start quote
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
							// this approach assumes few quotes; is quick for a few quotes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
							currentOffset++;
	
							start++;
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
						}
						currentOffset++;
					} while(true);
				}
				
				if(currentOffset > start) {
					consumer.consume(object, current, start, currentOffset);
				}
				
				if(current[currentOffset] != '\r') {
					while(current[++currentOffset] != '\r');
				}
				
				++currentOffset; // skip carriage return

				return ++currentOffset; // skip newline
			}
			
			public static int orSkip(char[] current, int currentOffset, CsvColumnValueConsumer consumer, Object object, char quoteCharacter, char escapeCharacter) throws IOException {
				if(current[currentOffset] != quoteCharacter) {
					return ClassicPlainColumn.Last.NewLineCarriageReturn.orSkip(current, currentOffset, consumer, object);
				}
				
				// now inside start quote
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
							// this approach assumes few quotes; is quick for a few quotes but more expensive for many
							System.arraycopy(current, start, current, start + 1, currentOffset - start);
	
							currentOffset++;
	
							start++;
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
						}
						currentOffset++;
					} while(true);
				}
				
				if(currentOffset > start) {
					consumer.consume(object, current, start, currentOffset);
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
	}

	@Override
	public void last(MethodVisitor mv, String subClassInternalName, boolean carriageReturn, boolean inline) {
		if(consumer == null) {
			throw new IllegalArgumentException();
		}
		
	}
	
}
