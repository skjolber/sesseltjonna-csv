package com.github.skjolber.stcsv.databinder;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.databinder.DynamicClassLoader;

public class CsvReaderClassLoaderTest {

	@Test
	public void throwsExceptionOnInvalidClass() {
		DynamicClassLoader loader = new DynamicClassLoader(getClass().getClassLoader(), new byte[] {}, "test");
		
		assertThrows(ClassNotFoundException.class, ()->{
			loader.findClass("my.test.class");
        } );
		
	}
}
