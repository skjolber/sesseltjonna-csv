package com.github.skjolber.stcsv.databinder;

import java.io.IOException;
import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.databinder.IgnoredColumn;

import static org.mockito.Mockito.*;
public class IgnoredColumnDifferentColumnQuoteAndEscapeCharacterTest {

	private String quotedNoEscapeNoLinebreak = "'abcdef','ghi',paydata\n";
	private String quotedNoEscapeLinebreak = "'abc\ndef','g\nhi',paydata\n";
	
	private String quotedEscapeNoLinebreak = "'abc\\'def','g\\'hi',paydata\n";
	private String quotedEscapeLinebreak = "'abc\\'\ndef','g\\'\nhi',paydata\n";
	
	private String quotedLinebreakFirstPart = "'abcdefghijiklmnopqrstuvwxyz";
	private String quotedLinebreakSecondPart = "h',ghi,paydata\n";
	
	
	@Test
	public void DifferentQuoteAndEscapeCharactersSkipNoEscape() throws IOException {
		AbstractCsvReader scanner = mock(AbstractCsvReader.class);
		when(scanner.getEndOfLineIndex()).thenReturn(1024);
		
		int offset = IgnoredColumn.DifferentQuoteAndEscapeCharacter.skipColumns(scanner, quotedNoEscapeLinebreak.toCharArray(), 0, ',', '\'', '\\', 2);
		assertThat(offset).isEqualTo(quotedNoEscapeLinebreak.lastIndexOf(',') + 1);
		
		offset = IgnoredColumn.DifferentQuoteAndEscapeCharacter.skipColumnsWithoutLinebreak(quotedNoEscapeNoLinebreak.toCharArray(), 0, ',', '\'', '\\', 2);
		assertThat(offset).isEqualTo(quotedNoEscapeNoLinebreak.lastIndexOf(',') + 1);
		
		offset = IgnoredColumn.DifferentQuoteAndEscapeCharacter.skipToLineBreak(scanner, quotedNoEscapeLinebreak.toCharArray(), 0, '\'', '\\');
		assertThat(offset).isEqualTo(quotedNoEscapeLinebreak.length());

		offset = IgnoredColumn.DifferentQuoteAndEscapeCharacter.skipToLineBreakWithoutLinebreak(quotedNoEscapeNoLinebreak.toCharArray(), 0, '\'', '\\');
		assertThat(offset).isEqualTo(quotedNoEscapeNoLinebreak.length());
	}
	
	@Test
	public void DifferentQuoteAndEscapeCharactersSkipEscaped() throws IOException {
		AbstractCsvReader scanner = mock(AbstractCsvReader.class);
		when(scanner.getEndOfLineIndex()).thenReturn(1024);
		
		int offset = IgnoredColumn.DifferentQuoteAndEscapeCharacter.skipColumns(scanner, quotedEscapeLinebreak.toCharArray(), 0, ',', '\'', '\\', 2);
		assertThat(offset).isEqualTo(quotedEscapeLinebreak.lastIndexOf(',') + 1);
		
		offset = IgnoredColumn.DifferentQuoteAndEscapeCharacter.skipColumnsWithoutLinebreak(quotedEscapeNoLinebreak.toCharArray(), 0, ',', '\'', '\\', 2);
		assertThat(offset).isEqualTo(quotedEscapeNoLinebreak.lastIndexOf(',') + 1);
		
		offset = IgnoredColumn.DifferentQuoteAndEscapeCharacter.skipToLineBreak(scanner, quotedEscapeLinebreak.toCharArray(), 0, '\'', '\\');
		assertThat(offset).isEqualTo(quotedEscapeLinebreak.length());

		offset = IgnoredColumn.DifferentQuoteAndEscapeCharacter.skipToLineBreakWithoutLinebreak(quotedEscapeNoLinebreak.toCharArray(), 0, '\'', '\\');
		assertThat(offset).isEqualTo(quotedEscapeNoLinebreak.length());
	}

	@Test
	public void testFillSkipColumns() throws IOException {
		AbstractCsvReader scanner = mock(AbstractCsvReader.class);
		when(scanner.getEndOfLineIndex()).thenReturn(quotedLinebreakFirstPart.length() - 1);
		
		char[] buffer = new char[1024];
		char[] first = quotedLinebreakFirstPart.toCharArray();
		System.arraycopy(first, 0, buffer, 0, first.length);
		
		when(scanner.fill(0)).thenAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				char[] second = quotedLinebreakSecondPart.toCharArray();
				System.arraycopy(second, 0, buffer, 0, second.length);
				
				return quotedLinebreakSecondPart.length();
			}
		});
		int offset = IgnoredColumn.DifferentQuoteAndEscapeCharacter.skipColumns(scanner, buffer, 0, ',', '\'', '\\',  2);
		assertThat(offset).isEqualTo(quotedLinebreakSecondPart.lastIndexOf(',') + 1);
		
	}

	@Test
	public void testFillSkipNewline() throws IOException {
		AbstractCsvReader scanner = mock(AbstractCsvReader.class);
		when(scanner.getEndOfLineIndex()).thenReturn(quotedLinebreakFirstPart.length() - 1);
		
		char[] buffer = new char[1024];
		char[] first = quotedLinebreakFirstPart.toCharArray();
		System.arraycopy(first, 0, buffer, 0, first.length);
		
		when(scanner.fill(0)).thenAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				char[] second = quotedLinebreakSecondPart.toCharArray();
				System.arraycopy(second, 0, buffer, 0, second.length);
				
				return quotedLinebreakSecondPart.length();
			}
		});
		int offset = IgnoredColumn.DifferentQuoteAndEscapeCharacter.skipToLineBreak(scanner, buffer, 0, '\'', '\\');
		assertThat(offset).isEqualTo(quotedLinebreakSecondPart.length());
	}

}
