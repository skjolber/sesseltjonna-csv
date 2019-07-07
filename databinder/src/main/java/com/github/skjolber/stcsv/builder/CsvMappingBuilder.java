package com.github.skjolber.stcsv.builder;

import java.util.List;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.column.bi.BooleanCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.column.bi.CsvFieldMapperBuilder;
import com.github.skjolber.stcsv.column.bi.DoubleCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.column.bi.IntCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.column.bi.LongCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.column.bi.StringCsvFieldMapperBuilder;

public class CsvMappingBuilder<T> extends AbstractCsvMappingBuilder<T, CsvMappingBuilder<T>> {

	public CsvMappingBuilder(Class<T> cls) {
		super(cls);
	}

	public CsvMapper<T> build() throws CsvBuilderException {
		List<AbstractColumn> columns = toColumns();
		
		ClassLoader classLoader = this.classLoader;
		if(classLoader == null) {
			classLoader = getDefaultClassLoader();
		}
		
		if(bufferLength < columns.size() * 2 + 1) {
			throw new CsvBuilderException("Expected buffer length at least " + (columns.size() * 2 + 1));
		}
		
		return new CsvMapper<T>(target, divider, quoteCharacter, escapeCharacter, columns, skipEmptyLines, skipComments, skippableFieldsWithoutLinebreaks, classLoader, bufferLength);
	}
	
	public CsvFieldMapperBuilder<T, CsvMappingBuilder<T>> field(String name) {
		return new CsvFieldMapperBuilder<T, CsvMappingBuilder<T>>(this, name);
	}

	public StringCsvFieldMapperBuilder<T, CsvMappingBuilder<T>> stringField(String name) {
		return new StringCsvFieldMapperBuilder<T, CsvMappingBuilder<T>>(this, name);
	}

	public DoubleCsvFieldMapperBuilder<T, CsvMappingBuilder<T>> doubleField(String name) {
		return new DoubleCsvFieldMapperBuilder<T, CsvMappingBuilder<T>>(this, name);
	}

	public LongCsvFieldMapperBuilder<T, CsvMappingBuilder<T>> longField(String name) {
		return new LongCsvFieldMapperBuilder<T, CsvMappingBuilder<T>>(this, name);
	}

	public IntCsvFieldMapperBuilder<T, CsvMappingBuilder<T>> integerField(String name) {
		return new IntCsvFieldMapperBuilder<T, CsvMappingBuilder<T>>(this, name);
	}

	public BooleanCsvFieldMapperBuilder<T, CsvMappingBuilder<T>> booleanField(String name) {
		return new BooleanCsvFieldMapperBuilder<T, CsvMappingBuilder<T>>(this, name);
	}

}
