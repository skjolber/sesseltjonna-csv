package com.github.skjolber.stcsv.builder;

import java.util.List;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.CsvMapper2;

public class CsvMappingBuilder2<T, D> extends AbstractCsvMappingBuilder<T, CsvMappingBuilder2<T, D>> {

	private Class<D> intermediate;
	
	public CsvMappingBuilder2(Class<T> cls, Class<D> intermediate) {
		super(cls);
		
		this.intermediate = intermediate;
	}

	public CsvMapper2<T, D> build() {
		List<AbstractColumn> columns = toColumns();
		
		ClassLoader classLoader = this.classLoader;
		if(classLoader == null) {
			classLoader = getDefaultClassLoader();
		}
		
		if(bufferLength <= 0) {
			throw new IllegalArgumentException("Expected positive buffer length");
		}
		
		return new CsvMapper2<T, D>(target, intermediate, divider, columns, skipEmptyLines, skipComments, skippableFieldsWithoutLinebreaks, classLoader, bufferLength);
	}

	public StringCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>> stringField(String name) {
		return new StringCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>>(this, name);
	}
	
	public Class<D> getIntermediate() {
		return intermediate;
	}


	/*
	public CsvFieldMapperBuilder<T, CsvMappingBuilder3<T>> field(String name) {
		return new CsvFieldMapperBuilder<T, CsvMappingBuilder3<T>>(this, name);
	}

	public DoubleCsvFieldMapperBuilder<T, CsvMappingBuilder3<T>> doubleField(String name) {
		return new DoubleCsvFieldMapperBuilder<T, CsvMappingBuilder3<T>>(this, name);
	}

	public LongCsvFieldMapperBuilder<T, CsvMappingBuilder3<T>> longField(String name) {
		return new LongCsvFieldMapperBuilder<T, CsvMappingBuilder3<T>>(this, name);
	}

	public IntCsvFieldMapperBuilder<T, CsvMappingBuilder3<T>> integerField(String name) {
		return new IntCsvFieldMapperBuilder<T, CsvMappingBuilder3<T>>(this, name);
	}

	public BooleanCsvFieldMapperBuilder<T, CsvMappingBuilder3<T>> booleanField(String name) {
		return new BooleanCsvFieldMapperBuilder<T, CsvMappingBuilder3<T>>(this, name);
	}
	*/

}
