package com.github.skjolber.stcsv.databinder.prototype.projection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.databinder.CsvMapper;
import com.github.skjolber.stcsv.databinder.projection.SetterValueProjection;

public class SetterValueProjectionTest {
	
	@Test
	public void testCreateSuccess() {
		String internalName = CsvMapper.getInternalName(getClass());
		assertNotNull(SetterValueProjection.newInstance(boolean.class, "booleanSetter", internalName));
		assertNotNull(SetterValueProjection.newInstance(int.class, "intSetter", internalName));
		assertNotNull(SetterValueProjection.newInstance(long.class, "longSetter", internalName));
		assertNotNull(SetterValueProjection.newInstance(double.class, "doubleSetter", internalName));
		assertNotNull(SetterValueProjection.newInstance(String.class, "stringSetter", internalName));
	}
	
	@Test
	public void testCreateFailure() throws Exception {
		assertThrows(CsvBuilderException.class, ()->{
			assertNotNull(SetterValueProjection.newInstance(SetterValueProjectionTest.class, "someSetter", "someClass"));
	    } );
	}	
}
