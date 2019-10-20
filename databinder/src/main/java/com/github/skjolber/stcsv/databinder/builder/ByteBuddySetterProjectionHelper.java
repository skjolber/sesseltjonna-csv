package com.github.skjolber.stcsv.databinder.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.github.skjolber.stcsv.builder.CsvBuilderException;

public class ByteBuddySetterProjectionHelper<T> extends SetterProjectionHelper<T> implements InvocationHandler {
		
	protected T proxy;

	public ByteBuddySetterProjectionHelper(Class<T> target) {
		super(target);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		this.method = method;
		
		return null;
	}

	protected T generateProxy() throws Exception {
		return (T) new net.bytebuddy.ByteBuddy()
				  .subclass(target)
				  .method(net.bytebuddy.matcher.ElementMatchers.any())
				  .intercept(net.bytebuddy.implementation.InvocationHandlerAdapter.of(this))
				  .make()
				  .load(target.getClassLoader()).getLoaded().getDeclaredConstructor().newInstance();		
	}


	protected Method invokeSetter(AbstractTypedCsvFieldMapperBuilder<T, ?> abstractCsvFieldMapperBuilder) throws CsvBuilderException {
		if(proxy == null) {
			try {
				proxy = generateProxy();
			} catch (Exception e) {
				throw new CsvBuilderException(e);
			}
		}
		abstractCsvFieldMapperBuilder.invokeSetter(proxy);
		
		return this.method;
	}	
	
	public Method toMethod(AbstractTypedCsvFieldMapperBuilder<T, ?> abstractCsvFieldMapperBuilder) throws CsvBuilderException {
		if(abstractCsvFieldMapperBuilder.hasSetter()) {
			// detect setter using proxy class
			return invokeSetter(abstractCsvFieldMapperBuilder); // populates the 'method' field
		} 
		// detect setter using reflection, based on the name
		return super.toMethod(abstractCsvFieldMapperBuilder);
	}	
}
