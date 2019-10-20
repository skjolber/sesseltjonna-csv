package com.github.skjolber.stcsv.databinder.column.tri;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.databinder.builder.CsvMappingBuilder2;
import com.github.skjolber.stcsv.databinder.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.databinder.column.bi.CsvFieldMapperBuilder;
import com.github.skjolber.stcsv.databinder.projection.TriConsumerProjection;
import com.github.skjolber.stcsv.databinder.projection.ValueProjection;

public class CsvFieldMapperTriBuilder<T, D, B extends CsvMappingBuilder2<T, ?>> extends CsvFieldMapperBuilder<T, B> {

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

	protected ValueProjection getProjection(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException {
		if(triConsumer != null) {
			return new TriConsumerProjection(triConsumer, index);
		}
		return super.getProjection(index, proxy);
	}

}

