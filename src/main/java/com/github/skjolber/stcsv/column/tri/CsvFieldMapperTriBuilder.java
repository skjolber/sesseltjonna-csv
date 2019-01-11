package com.github.skjolber.stcsv.column.tri;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.builder.CsvMappingBuilder2;
import com.github.skjolber.stcsv.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.column.bi.StringCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.projection.TriConsumerProjection;

public class CsvFieldMapperTriBuilder<T, D, B extends CsvMappingBuilder2<T, ?>> extends StringCsvFieldMapperBuilder<T, B> {

	protected CsvColumnValueTriConsumer<T, D> triConsumer;
	protected Class<D> intermediate;
	
	public CsvFieldMapperTriBuilder(B parent, String name, Class<D> intermediate) {
		super(parent, name);
		
		this.intermediate = intermediate; 
	}

	public CsvFieldMapperTriBuilder<T, D, B> consumer(CsvColumnValueTriConsumer<T, D> consumer) {
		this.triConsumer = consumer;
		
		return this;
	}

	protected void buildProjection(AbstractColumn column, SetterProjectionHelper<T> proxy, int index) {
		if(triConsumer != null) {
			column.setProjection(new TriConsumerProjection(triConsumer, index));
		} else {
			super.buildProjection(column, proxy, index);
		}
	}

}

