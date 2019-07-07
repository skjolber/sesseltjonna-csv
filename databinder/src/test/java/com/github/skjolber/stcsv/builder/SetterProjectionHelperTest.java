package com.github.skjolber.stcsv.builder;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import static com.google.common.truth.Truth.*;
import com.github.skjolber.stcsv.prototype.CsvLineObject;

public class SetterProjectionHelperTest {

	@Test
	public void normalizedSetterName() {
		assertThat(SetterProjectionHelper.getSetterName("type")).isEqualTo("setType");
		assertThat(SetterProjectionHelper.getNormalizedSetterName("type_of_meat")).isEqualTo("setTypeOfMeat");
		assertThat(SetterProjectionHelper.getNormalizedSetterName("type")).isEqualTo("setType");
	}
	
	@Test
	public void unknownSetterName() {
		SetterProjectionHelper helper = new SetterProjectionHelper(CsvLineObject.class);
		
		assertThrows(CsvBuilderException.class, ()->{
			helper.invokeSetter("abcd", Object.class);
	    } );
	}
	
	@Test
	public void normalizesSetterName() {
		SetterProjectionHelper helper = new SetterProjectionHelper(CsvLineObject.class);
		helper.invokeSetter("string_value", String.class);
	}
	
}
