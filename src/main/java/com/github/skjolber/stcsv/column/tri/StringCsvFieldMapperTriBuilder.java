package com.github.skjolber.stcsv.column.tri;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.builder.CsvMappingBuilder2;
import com.github.skjolber.stcsv.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.column.bi.StringCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.projection.TriConsumerProjection;

public class StringCsvFieldMapperTriBuilder<T, D, B extends CsvMappingBuilder2<T, ?>> extends StringCsvFieldMapperBuilder<T, B> {

	protected TriConsumer<T, D, String> triConsumer;
	protected Class<D> intermediate;
	
	public StringCsvFieldMapperTriBuilder(B parent, String name, Class<D> intermediate) {
		super(parent, name);
		
		this.intermediate = intermediate; 
	}

	public StringCsvFieldMapperTriBuilder<T, D, B> consumer(TriConsumer<T, D, String> consumer) {
		this.triConsumer = consumer;
		
		return this;
	}

	protected void buildProjection(AbstractColumn column, SetterProjectionHelper<T> proxy, int index) {
		if(triConsumer != null) {
			column.setProjection(new TriConsumerProjection(new StringCsvColumnValueTriConsumer<>(triConsumer), index));
		} else {
			super.buildProjection(column, proxy, index);
		}
	}

}

