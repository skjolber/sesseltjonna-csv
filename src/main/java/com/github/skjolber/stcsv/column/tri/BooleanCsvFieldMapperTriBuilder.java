package com.github.skjolber.stcsv.column.tri;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.builder.CsvMappingBuilder2;
import com.github.skjolber.stcsv.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.column.bi.LongCsvFieldMapperBuilder;

public class BooleanCsvFieldMapperTriBuilder<T, D, B extends CsvMappingBuilder2<T, ?>> extends LongCsvFieldMapperBuilder<T, B> {

	protected ObjBooleanTriConsumer<T, D> triConsumer;
	protected Class<D> intermediate;
	
	public BooleanCsvFieldMapperTriBuilder(B parent, String name, Class<D> intermediate) {
		super(parent, name);
		
		this.intermediate = intermediate; 
	}

	public BooleanCsvFieldMapperTriBuilder<T, D, B> consumer(ObjBooleanTriConsumer<T, D>  consumer) {
		this.triConsumer = consumer;
		
		return this;
	}

	protected void buildProjection(AbstractColumn column, SetterProjectionHelper<T> proxy) {
		if(triConsumer != null) {
			column.setTriConsumer(new BooleanCsvColumnValueTriConsumer<>(triConsumer), parent.getIntermediate());
		} else {
			super.buildProjection(column, proxy);
		}
	}
	
	
	

}


