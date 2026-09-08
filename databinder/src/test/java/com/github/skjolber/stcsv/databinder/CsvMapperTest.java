package com.github.skjolber.stcsv.databinder;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvException;
import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.EmptyCsvReader;
import com.github.skjolber.stcsv.databinder.CsvMapper;
import com.github.skjolber.stcsv.databinder.StaticCsvMapper;
import com.github.skjolber.stcsv.databinder.prototype.CsvLineObject;

public class CsvMapperTest {

	private CsvMapper<CsvLineObject> consumerMapping;
	private String header = "a,b,c,d,e,f,g,h";
	private String line = "aa,1,2,d,e,true,1.0,h\n";
	
	@BeforeEach
	public void init() throws Exception {
		consumerMapping = CsvMapper.builder(CsvLineObject.class)
				.stringField("a")
					.consumer(CsvLineObject::setStringValue)
					.optional()
				.longField("b")
					.consumer(CsvLineObject::setLongValue)
					.required()
				.integerField("c")
					.consumer(CsvLineObject::setIntegerValue)
					.optional()
				.booleanField("f")
					.consumer(CsvLineObject::setBooleanValue)
					.optional()
				.doubleField("h")
					.consumer(CsvLineObject::setDoubleValue)
					.optional()
				.build();
	}
	
	@Test
	public void testBuild1() throws Exception {
		createReaders(consumerMapping.buildDefaultStaticCsvMapper(false));
		createReaders(consumerMapping.buildStaticCsvMapper(false, header));
		createReaders(consumerMapping.buildStaticCsvMapper(header + "\n"));
	}
	
	private void createReaders(StaticCsvMapper<CsvLineObject> csvMapper) {
		csvMapper.newInstance(new StringReader(line));
		
		char[] charArray = line.toCharArray();
		csvMapper.newInstance(new StringReader(line), charArray, 0, charArray.length);
	}

	@Test
	public void testBuild2() throws Exception {
		createReaders(consumerMapping.buildStaticCsvMapper(false, header));
		createReaders(consumerMapping.buildStaticCsvMapper(header + "\r\n"));
	}
	
	@Test
	public void testFirstLineMustHaveLinebreak() throws Exception {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < 100*1024; i++) {
			builder.append('a');
		}
			
		assertThrows(CsvException.class, ()->{
			consumerMapping.create(new StringReader(builder.toString()));
        } );
		
	}

	@Test
	public void testCreateWithPartialHeaderReads() throws Exception {
		try (CsvReader<CsvLineObject> reader = consumerMapping.create(new ChunkedStringReader(header + "\n" + "aa,1,2,d,e,true,1.0,2.5\n", 2))) {
			CsvLineObject value = reader.next();
			assertThat(value.getStringValue()).isEqualTo("aa");
			assertThat(value.getLongValue()).isEqualTo(1L);
			assertThat(reader.next()).isNull();
		}
	}
	
	@Test
	public void testReaderInputWithNoMappedColumns() throws Exception {
		CsvReader<CsvLineObject> csvReader1 = consumerMapping.buildStaticCsvMapper(false, "x,y,z").newInstance(new StringReader("a,b,c\n"));
		assertThat(csvReader1).isInstanceOf(EmptyCsvReader.class);
		
		char[] charArray = line.toCharArray();
		CsvReader<CsvLineObject> csvReader2 = consumerMapping.buildStaticCsvMapper(false, "x,y,z").newInstance(new StringReader(line), charArray, 0, charArray.length);
		assertThat(csvReader2).isInstanceOf(EmptyCsvReader.class);
	}	

	private static class ChunkedStringReader extends StringReader {

		private final int chunkSize;

		private ChunkedStringReader(String input, int chunkSize) {
			super(input);
			this.chunkSize = chunkSize;
		}

		@Override
		public int read(char[] buffer, int offset, int length) throws java.io.IOException {
			return super.read(buffer, offset, Math.min(length, chunkSize));
		}
	}
	
}
