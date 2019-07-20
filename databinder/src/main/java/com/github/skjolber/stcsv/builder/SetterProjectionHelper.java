package com.github.skjolber.stcsv.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SetterProjectionHelper<T> {
	
	protected Class<T> target;
	protected Method method;
	protected T proxy;
	
	public SetterProjectionHelper(Class<T> target) {
		this.target = target;
	}
	
	public void detectSetter(String name, Class<?> cls) {
		try {
			this.method = target.getMethod(getSetterName(name), cls);
		} catch (NoSuchMethodException e1) {
			try {
				this.method = target.getMethod(getNormalizedSetterName(name), cls);
			} catch (NoSuchMethodException e2) {
				throw new CsvBuilderException("Unable to detect setter for class " + target.getName() + " field '" + name + "' (" + getSetterName(name) + "/ "+ getNormalizedSetterName(name) + ").");
			}
		}
	}
	
	protected static String getSetterName(String name) {
		return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	protected static String getNormalizedSetterName(String name) {
		
		StringBuilder builder = new StringBuilder("set");
		
		boolean high = true;
		for(int i = 0; i < name.length(); i++) {
			if(high) {
				builder.append(Character.toUpperCase(name.charAt(i)));
				
				high = false;
			} else if(name.charAt(i) == '_') {
				high = true;
			} else {
				builder.append(name.charAt(i));
			}
		}
		
		return builder.toString();
	}

	public Method toMethod(AbstractTypedCsvFieldMapperBuilder<T, ?> abstractCsvFieldMapperBuilder) throws CsvBuilderException {
		try {
			// detect setter using reflection, based on the name
			detectSetter(abstractCsvFieldMapperBuilder.getName(), abstractCsvFieldMapperBuilder.getColumnClass());
			
			return method;
		} finally {
			method = null;
		}
	}

}
