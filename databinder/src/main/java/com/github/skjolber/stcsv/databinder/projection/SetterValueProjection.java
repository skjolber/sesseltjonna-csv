package com.github.skjolber.stcsv.databinder.projection;

import org.objectweb.asm.MethodVisitor;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.databinder.AbstractCsvMapper;

public abstract class SetterValueProjection implements ValueProjection {

	protected static final int currentOffsetIndex = AbstractCsvMapper.VAR_CURRENT_OFFSET;
	protected static final int currentArrayIndex = AbstractCsvMapper.VAR_CURRENT_ARRAY;
	protected static final int objectIndex = AbstractCsvMapper.VAR_OBJECT;
	protected static final int startIndex = AbstractCsvMapper.VAR_START;
	protected static final int rangeIndex = AbstractCsvMapper.VAR_RANGE;
	protected static final int intermediateIndex = AbstractCsvMapper.VAR_INTERMEDIATE_OBJECT;

	public static SetterValueProjection newInstance(Class<?> setterClass, String setterName, String mappedClassInternalName) {
		if(setterClass == String.class) {
			return new ObjectSetterValueProjection(setterName, mappedClassInternalName, "java/lang/String");
		} else if(setterClass == int.class) {
			return new PrimitiveSetterValueProjection(setterName, mappedClassInternalName, "com/github/skjolber/stcsv/databinder/column/bi/IntCsvColumnValueConsumer", "parseInt", 'I');
		} else if(setterClass == long.class) {
			return new PrimitiveSetterValueProjection(setterName, mappedClassInternalName, "com/github/skjolber/stcsv/databinder/column/bi/LongCsvColumnValueConsumer", "parseLong", 'J');
		} else if(setterClass == boolean.class) {
			return new PrimitiveSetterValueProjection(setterName, mappedClassInternalName, "com/github/skjolber/stcsv/databinder/column/bi/BooleanCsvColumnValueConsumer", "parseBoolean", 'Z');
		} else if(setterClass == double.class) {
			return new PrimitiveSetterValueProjection(setterName, mappedClassInternalName, "com/github/skjolber/stcsv/databinder/column/bi/DoubleCsvColumnValueConsumer", "parseDouble", 'D');
		} else {
			throw new CsvBuilderException("No setter for type " + setterClass.getName());
		}
	}
	
	protected final String setterName;
	protected final String mappedClassInternalName;
	
	public SetterValueProjection(String setterName, String mappedClassInternalName) {
		this.setterName = setterName;
		this.mappedClassInternalName = mappedClassInternalName;
	}
	
	public abstract void write(MethodVisitor mv, String subClassInternalName, int endIndex);

}