package com.github.skjolber.stcsv.builder;

import java.util.List;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.CsvMapper2;
import com.github.skjolber.stcsv.column.tri.BooleanCsvFieldMapperTriBuilder;
import com.github.skjolber.stcsv.column.tri.CsvFieldMapperTriBuilder;
import com.github.skjolber.stcsv.column.tri.DoubleCsvFieldMapperTriBuilder;
import com.github.skjolber.stcsv.column.tri.IntCsvFieldMapperTriBuilder;
import com.github.skjolber.stcsv.column.tri.LongCsvFieldMapperTriBuilder;
import com.github.skjolber.stcsv.column.tri.StringCsvFieldMapperTriBuilder;

public class CsvMappingBuilder2<T, D> extends AbstractCsvMappingBuilder<T, CsvMappingBuilder2<T, D>> {

	private Class<D> intermediate;
	
	public CsvMappingBuilder2(Class<T> cls, Class<D> intermediate) {
		super(cls);
		
		this.intermediate = intermediate;
	}

	public CsvMapper2<T, D> build() throws CsvBuilderException {
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
		return new StringCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>>(this, name, intermediate);
	}
	
	public Class<D> getIntermediate() {
		return intermediate;
	}

	public CsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>> field(String name) {
		return new CsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>>(this, name, intermediate);
	}

	public DoubleCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>> doubleField(String name) {
		return new DoubleCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>>(this, name, intermediate);
	}
	
	public LongCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>> longField(String name) {
		return new LongCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>>(this, name, intermediate);
	}
	
	public IntCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>> integerField(String name) {
		return new IntCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>>(this, name, intermediate);
	}

	public BooleanCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>> booleanField(String name) {
		return new BooleanCsvFieldMapperTriBuilder<T, D, CsvMappingBuilder2<T, D>>(this, name, intermediate);
	}

}
