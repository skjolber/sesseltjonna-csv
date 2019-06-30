package com.github.skjolber.stcsv.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.skjolber.stcsv.AbstractColumn;

public abstract class AbstractCsvMappingBuilder<T, B extends AbstractCsvMappingBuilder<T, ?>> extends AbstractCsvBuilder<B>  {

	protected Class<T> target;
	
	protected ClassLoader classLoader;

	protected List<AbstractCsvFieldMapperBuilder<T, ? extends AbstractCsvMappingBuilder<T, ?>>> fields = new ArrayList<>();

	public AbstractCsvMappingBuilder(Class<T> cls) {
		this.target = cls;
	}
	
	public B classLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		
		return (B) this;
	}

	protected List<AbstractColumn> toColumns() throws CsvBuilderException {
		List<AbstractColumn> columns = new ArrayList<>(fields.size());
		Set<String> fieldNames = new HashSet<>(fields.size() * 2);

		SetterProjectionHelper<T> proxy;
		if(byteBuddy) {
			proxy = new ByteBuddySetterProjectionHelper<T>(target);
		} else {
			proxy = new SetterProjectionHelper<T>(target);
		}
		
		for (int i = 0; i < fields.size(); i++) {
			AbstractCsvFieldMapperBuilder<T, ?> builder = fields.get(i);
			
			String name = builder.getName();
			if(fieldNames.contains(name)) {
				throw new CsvBuilderException("Duplicate field '" + name + "'");
			}
			fieldNames.add(name);
			
			if(builder.isLinebreaks() && !builder.isQuoted()) {
				throw new CsvBuilderException();
			}

			columns.add(builder.build(i, proxy));
		}
		return columns;
	}

	protected ClassLoader getDefaultClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	protected B field(AbstractCsvFieldMapperBuilder<T, ? extends AbstractCsvMappingBuilder<T, ?>> field) {
		this.fields.add(field);
		
		return (B) this;
	}

	public Class<T> getTarget() {
		return target;
	}	
}
