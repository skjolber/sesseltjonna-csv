package com.github.skjolber.stcsv.databinder;

import java.util.Objects;

class DynamicClassLoader extends ClassLoader {
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