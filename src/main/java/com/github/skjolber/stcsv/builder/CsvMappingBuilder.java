package com.github.skjolber.stcsv.builder;

import java.util.List;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.CsvMapper;

public class CsvMappingBuilder<T> extends AbstractCsvMappingBuilder<T, CsvMappingBuilder<T>> {

	public CsvMappingBuilder(Class<T> cls) {
		super(cls);
	}
	

	public CsvMapper<T> build() {
		List<AbstractColumn> columns = toColumns();
		
		ClassLoader classLoader = this.classLoader;
		if(classLoader == null) {
			classLoader = getDefaultClassLoader();
		}
		
		if(bufferLength <= 0) {
			throw new IllegalArgumentException("Expected positive buffer length");
		}
		
		return new CsvMapper<T>(target, divider, columns, skipEmptyLines, skipComments, skippableFieldsWithoutLinebreaks, classLoader, bufferLength);
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
