package com.github.skjolber.stcsv.databinder;

public class CsvReaderClassLoader<T> {
	
    protected final ClassLoader contextClassLoader;

    public CsvReaderClassLoader(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends T> load(byte[] classBytes, String className) throws Exception {
        return (Class<? extends T>) new DynamicClassLoader(contextClassLoader, classBytes, className).loadClass(className);
    }
}