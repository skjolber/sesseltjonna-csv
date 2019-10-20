package com.github.skjolber.stcsv.databinder.column.tri;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.databinder.builder.CsvMappingBuilder2;
import com.github.skjolber.stcsv.databinder.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.databinder.column.bi.LongCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.databinder.projection.TriConsumerProjection;
import com.github.skjolber.stcsv.databinder.projection.ValueProjection;

public class LongCsvFieldMapperTriBuilder<T, D, B extends CsvMappingBuilder2<T, ?>> extends LongCsvFieldMapperBuilder<T, B> {

	protected ObjLongTriConsumer<T, D> triConsumer;
	protected Class<D> intermediate;
	
	public LongCsvFieldMapperTriBuilder(B parent, String name, Class<D> intermediate) {
		super(parent, name);
		
		this.intermediate = intermediate; 
	}

	public LongCsvFieldMapperTriBuilder<T, D, B> consumer(ObjLongTriConsumer<T, D>  consumer) {
		this.triConsumer = consumer;
		
		return this;
	}

	protected ValueProjection getProjection(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException {
		if(triConsumer != null) {
			return new TriConsumerProjection(new LongCsvColumnValueTriConsumer<>(triConsumer), index);
		}
		return super.getProjection(index, proxy);
	}

}


