package com.github.skjolber.stcsv;

import java.util.Objects;

public class CsvReaderClassLoader<T> {
	
    protected final ClassLoader contextClassLoader;

    public CsvReaderClassLoader(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends T> load(byte[] classBytes, String className) throws Exception {
        return (Class<? extends T>) new DynamicClassLoader(contextClassLoader, classBytes, className).loadClass(className);
    }

    protected static class DynamicClassLoader extends ClassLoader {
    	protected byte[] classBytes;
    	protected final String className;

    	protected DynamicClassLoader(ClassLoader contextClassLoader, byte[] classBytes, String className) {
            super(contextClassLoader);
            this.classBytes = classBytes;
            this.className = className;
        }

        @Override
        public Class<?> findClass(String className) throws ClassNotFoundException {
            if (Objects.equals(this.className, className)) {
                return defineClass(className, this.classBytes, 0, this.classBytes.length);
            }

            throw new ClassNotFoundException(className);
        }
    }
}