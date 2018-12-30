package com.github.skjolber.stcsv.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.skjolber.stcsv.AbstractColumn;
import com.github.skjolber.stcsv.AbstractCsvReader;
import com.github.skjolber.stcsv.CsvMapper;

public abstract class AbstractCsvMappingBuilder<T, B extends AbstractCsvMappingBuilder> implements InvocationHandler {

	protected static boolean isSafeByteUTF8Delimiter(char c) {
		//https://en.wikipedia.org/wiki/UTF-8#Description
		// all char 2, 3 and 4 are negative numbers in UTF-8
		return (int)c >= 0;
	}
	
	protected static boolean isSafeCharDelimiter(char c) {
		return !Character.isLowSurrogate(c);
	}
	
	protected Class<T> target;
	protected char divider = ',';
	protected char quoteCharacter = '"';
	protected char escapeCharacter = '"';
	
	protected boolean skipComments = false;
	protected boolean skipEmptyLines = false;
	protected boolean skippableFieldsWithoutLinebreaks = false;
	protected int bufferLength = AbstractCsvReader.DEFAULT_RANGE_LENGTH;
	
	protected ClassLoader classLoader;

	protected List<AbstractCsvFieldMapperBuilder<T, ? extends AbstractCsvMappingBuilder<T, B>>> fields = new ArrayList<>();

	protected Method method;

	public AbstractCsvMappingBuilder(Class<T> cls) {
		this.target = cls;
	}
	
	public B skipEmptyLines() {
		this.skipEmptyLines = true;
		
		return (B) this;
	}

	public B skipComments() {
		this.skipComments = true;
		
		return (B) this;
	}

	/**
	 * 
	 * All fields (including any ignored columns) are without linebreaks.
	 * 
	 * @return this
	 */

	public B skippableFieldsWithoutLinebreaks() {
		this.skippableFieldsWithoutLinebreaks = true;
		
		return (B) this;
	}
	
	public B classLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		
		return (B) this;
	}
	
	public B bufferLength(int length) {
		this.bufferLength = length;
		
		return (B) this;
	}
	
	public B divider(char c) {
		if(!isSafeCharDelimiter(c) || c == '\n') {
			throw new IllegalArgumentException("Cannot use character '" + c + "' as divider");
		}
		this.divider = c;
		
		return (B) this;
	}
	
	public B quoteCharacter(char c) {
		this.quoteCharacter = c;
		
		return (B) this;
	}
	
	public B escapeCharacter(char c) {
		this.escapeCharacter = c;
		
		return (B) this;
	}

	protected List<AbstractColumn> toColumns() {
		List<AbstractColumn> columns = new ArrayList<>(fields.size());
		Set<String> fieldNames = new HashSet<>(fields.size() * 2);
		
		T proxy = null;
		
		for (int i = 0; i < fields.size(); i++) {
			AbstractCsvFieldMapperBuilder<T, ?> builder = fields.get(i);
			
			String name = builder.getName();
			if(fieldNames.contains(name)) {
				throw new IllegalArgumentException("Duplicate field '" + name + "'");
			}
			fieldNames.add(name);
			
			if(builder.isLinebreaks() && !builder.isQuoted()) {
				throw new RuntimeException();
			}

			AbstractColumn build = builder.build(i);

			if(!builder.hasBiConsumer() && !builder.hasTriConsumer()) {
				if(builder.hasSetter()) {
					// detect setter using proxy class
					if(proxy == null) {
						try {
							proxy = generateProxy();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
					builder.invokeSetter(proxy); // populates the 'method' field
				} else {
					// detect setter using reflection, based on the name
					try {
						this.method = target.getMethod(builder.getSetterName(), builder.getColumnClass());
					} catch (NoSuchMethodException e1) {
						try {
							this.method = target.getMethod(builder.getNormalizedSetterName(), builder.getColumnClass());
						} catch (NoSuchMethodException e2) {
							throw new IllegalArgumentException("Unable to detect setter for class " + target.getName() + " field '" + builder.getName() + "' (" + builder.getSetterName() + "/ "+ builder.getNormalizedSetterName() + ").");
						}
					}
				}
				build.setSetter(method.getName(), method.getParameterTypes()[0]);
				
				this.method = null;
			}

			columns.add(build);
		}
		return columns;
	}

	protected ClassLoader getDefaultClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	protected T generateProxy() throws Exception {
		return (T) new net.bytebuddy.ByteBuddy()
				  .subclass(target)
				  .method(net.bytebuddy.matcher.ElementMatchers.any())
				  .intercept(net.bytebuddy.implementation.InvocationHandlerAdapter.of(this))
				  .make()
				  .load(target.getClassLoader()).getLoaded().newInstance();		
	}
	
	public Class<T> getTarget() {
		return target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		this.method = method;
		
		return null;
	}
	
	public char getEscapeCharacter() {
		return escapeCharacter;
	}
	
	public char getQuoteCharacter() {
		return quoteCharacter;
	}

	protected B field(AbstractCsvFieldMapperBuilder<T, ? extends AbstractCsvMappingBuilder<T, B>> field) {
		this.fields.add(field);
		
		return (B) this;
	}
}
