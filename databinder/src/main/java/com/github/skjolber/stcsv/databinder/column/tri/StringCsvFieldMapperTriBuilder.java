package com.github.skjolber.stcsv.databinder.column.tri;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.databinder.builder.CsvMappingBuilder2;
import com.github.skjolber.stcsv.databinder.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.databinder.column.bi.StringCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.databinder.projection.TriConsumerProjection;
import com.github.skjolber.stcsv.databinder.projection.ValueProjection;

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

	protected ValueProjection getProjection(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException {
		if(triConsumer != null) {
			return new TriConsumerProjection(new StringCsvColumnValueTriConsumer<>(triConsumer), index);
		}
		return super.getProjection(index, proxy);
	}

}

