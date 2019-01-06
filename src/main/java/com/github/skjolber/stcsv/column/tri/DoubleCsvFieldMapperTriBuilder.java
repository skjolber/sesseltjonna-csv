package com.github.skjolber.stcsv.column.tri;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.builder.CsvMappingBuilder2;
import com.github.skjolber.stcsv.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.column.bi.LongCsvFieldMapperBuilder;

public class DoubleCsvFieldMapperTriBuilder<T, D, B extends CsvMappingBuilder2<T, ?>> extends LongCsvFieldMapperBuilder<T, B> {

	protected ObjDoubleTriConsumer<T, D> triConsumer;
	protected Class<D> intermediate;
	
	public DoubleCsvFieldMapperTriBuilder(B parent, String name, Class<D> intermediate) {
		super(parent, name);
		
		this.intermediate = intermediate; 
	}

	public DoubleCsvFieldMapperTriBuilder<T, D, B> consumer(ObjDoubleTriConsumer<T, D>  consumer) {
		this.triConsumer = consumer;
		
		return this;
	}

	protected void buildProjection(AbstractColumn column, SetterProjectionHelper<T> proxy) {
		if(triConsumer != null) {
			column.setTriConsumer(new DoubleCsvColumnValueTriConsumer<>(triConsumer), parent.getIntermediate());
		} else {
			super.buildProjection(column, proxy);
		}
	}
	
	
	

}


