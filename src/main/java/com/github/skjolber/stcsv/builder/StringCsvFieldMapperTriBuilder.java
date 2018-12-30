package com.github.skjolber.stcsv.builder;

import com.github.skjolber.stcsv.column.tri.TriConsumer;

public class StringCsvFieldMapperTriBuilder<T, D, B extends AbstractCsvMappingBuilder> extends StringCsvFieldMapperBuilder<T, B> {

	public StringCsvFieldMapperTriBuilder(B parent, String name) {
		super(parent, name);
	}

	protected TriConsumer<T, D, String> triConsumer;

	public StringCsvFieldMapperTriBuilder<T, D, B> consumer(TriConsumer<T, D, String> consumer) {
		this.triConsumer = consumer;
		
		return this;
	}

	public boolean hasTriConsumer() {
		return triConsumer != null;
	}

}

