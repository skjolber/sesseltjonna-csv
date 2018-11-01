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
import com.github.skjolber.stcsv.column.CsvColumnValueConsumer;

public class CsvMappingBuilder<T> implements InvocationHandler {

	protected static boolean isSafeByteUTF8Delimiter(char c) {
		//https://en.wikipedia.org/wiki/UTF-8#Description
		// all char 2, 3 and 4 are negative numbers in UTF-8
		return (int)c >= 0;
	}
	
	protected static boolean isSafeCharDelimiter(char c) {
		return !Character.isLowSurrogate(c);
	}
	
	private Class<T> target;
	private char divider = ',';
	private char quoteCharacter = '"';
	private char escapeCharacter = '"';
	
	private boolean skipEmptyLines = false;
	private boolean skippableFieldsWithoutLinebreaks = false;
	private int bufferLength = AbstractCsvReader.DEFAULT_RANGE_LENGTH;
	
	private ClassLoader classLoader;

	private List<AbstractCsvFieldMapperBuilder<T>> fields = new ArrayList<>();
	private Method method;
	

	public CsvMappingBuilder(Class<T> cls) {
		this.target = cls;
	}
	
	public CsvMappingBuilder<T> skipEmptyLines() {
		this.skipEmptyLines = true;
		
		return this;
	}
	
	/**
	 * 
	 * All fields (including any ignored columns) are without linebreaks.
	 * 
	 * @return this
	 */

	public CsvMappingBuilder<T> skippableFieldsWithoutLinebreaks() {
		this.skippableFieldsWithoutLinebreaks = true;
		
		return this;
	}
	
	public CsvMappingBuilder<T> classLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		
		return this;
	}
	
	public CsvMappingBuilder<T> bufferLength(int length) {
		this.bufferLength = length;
		
		return this;
	}
	
	public CsvMappingBuilder<T> divider(char c) {
		if(!isSafeCharDelimiter(c) || c == '\n') {
			throw new IllegalArgumentException("Cannot use character '" + c + "' as divider");
		}
		this.divider = c;
		
		return this;
	}
	
	public CsvMappingBuilder<T> quoteCharacter(char c) {
		this.quoteCharacter = c;
		
		return this;
	}
	
	public CsvMappingBuilder<T> escapeCharacter(char c) {
		this.escapeCharacter = c;
		
		return this;
	}
	
	public CsvFieldMapperBuilder<T> field(String name) {
		return new CsvFieldMapperBuilder<T>(this, name);
	}

	public StringCsvFieldMapperBuilder<T> stringField(String name) {
		return new StringCsvFieldMapperBuilder<T>(this, name);
	}

	public DoubleCsvFieldMapperBuilder<T> doubleField(String name) {
		return new DoubleCsvFieldMapperBuilder<T>(this, name);
	}

	public LongCsvFieldMapperBuilder<T> longField(String name) {
		return new LongCsvFieldMapperBuilder<T>(this, name);
	}

	public IntCsvFieldMapperBuilder<T> integerField(String name) {
		return new IntCsvFieldMapperBuilder<T>(this, name);
	}

	public BooleanCsvFieldMapperBuilder<T> booleanField(String name) {
		return new BooleanCsvFieldMapperBuilder<T>(this, name);
	}

	protected CsvMappingBuilder<T> field(AbstractCsvFieldMapperBuilder<T> field) {
		this.fields.add(field);
		
		return this;
	}
	
	public CsvMapper<T> build() {
		List<AbstractColumn> columns = new ArrayList<>(fields.size());
		Set<String> fieldNames = new HashSet<>(fields.size() * 2);
		
		T proxy = null;
		
		for (int i = 0; i < fields.size(); i++) {
			AbstractCsvFieldMapperBuilder<T> builder = fields.get(i);
			
			String name = builder.getName();
			if(fieldNames.contains(name)) {
				throw new IllegalArgumentException("Duplicate field '" + name + "'");
			}
			fieldNames.add(name);
			
			if(builder.isLinebreaks() && !builder.isQuoted()) {
				throw new RuntimeException();
			}

			CsvColumnValueConsumer<T> valueConsumer = builder.getValueConsumer();
			
			AbstractColumn build = builder.build(i);

			if(valueConsumer == null) {
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
			} else {
				if(builder.hasSetter()) {
					throw new IllegalArgumentException("Setters and consumers cannot be combined for field '" + builder.getName() + "'.");
				}
				build.setConsumer(valueConsumer);
			}

			columns.add(build);
		}
		
		ClassLoader classLoader = this.classLoader;
		if(classLoader == null) {
			classLoader = getDefaultClassLoader();
		}
		
		if(bufferLength <= 0) {
			throw new IllegalArgumentException("Expected positive buffer length");
		}
		
		return new CsvMapper<T>(target, divider, quoteCharacter, escapeCharacter, columns, skipEmptyLines, skippableFieldsWithoutLinebreaks, classLoader, bufferLength);
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
}
