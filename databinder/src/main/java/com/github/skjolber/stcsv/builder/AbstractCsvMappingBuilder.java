package com.github.skjolber.stcsv.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.skjolber.stcsv.AbstractColumn;

public abstract class AbstractCsvMappingBuilder<T, B extends AbstractCsvMappingBuilder<T, ?>> extends AbstractCsvBuilder<B>  {

	protected static final boolean byteBuddyPresent;
	
	static {
		boolean present;
		try {
			Class.forName("net.bytebuddy.ByteBuddy");
			
			present = true;
		} catch(Exception e) {
			present = false;
		}
		byteBuddyPresent = present;
	}
	
	protected boolean skippableFieldsWithoutLinebreaks = false;

	protected Class<T> target;
	
	protected ClassLoader classLoader;

	protected List<AbstractCsvFieldMapperBuilder<T, ? extends AbstractCsvMappingBuilder<T, ?>>> fields = new ArrayList<>();

	protected boolean byteBuddy = byteBuddyPresent;
	
	// for testing
	B withoutByteBuddy() {
		byteBuddy = false;
		
		return (B) this;
	}
	
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
				throw new CsvBuilderException("Duplicate field '" + name + "'"); // strictly nothing wrong with mapping the same column multiple times
			}
			fieldNames.add(name);

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
	

	/**
	 * 
	 * All fields (including any ignored columns) are without linebreaks.
	 * 
	 * @return this
	 */

	@SuppressWarnings("unchecked")
	public B skippableFieldsWithoutLinebreaks() {
		this.skippableFieldsWithoutLinebreaks = true;
		
		return (B) this;
	}

}
