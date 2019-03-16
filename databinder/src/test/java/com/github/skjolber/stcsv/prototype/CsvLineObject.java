package com.github.skjolber.stcsv.prototype;

public class CsvLineObject {

	private String stringValue;
	private Long longValue;
	private Integer integerValue;
	private Short shortValue;
	private Byte byteValue;
	private Boolean booleanValue;
	private Character characterValue;

	private Double doubleValue;
	private Float floatValue;
	
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public Long getLongValue() {
		return longValue;
	}
	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}

	public Short getShortValue() {
		return shortValue;
	}
	public void setShortValue(short shortValue) {
		this.shortValue = shortValue;
	}
	public Byte getByteValue() {
		return byteValue;
	}
	public void setByteValue(byte byteValue) {
		this.byteValue = byteValue;
	}
	public Boolean getBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public Float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}
	public Integer getIntegerValue() {
		return integerValue;
	}
	public void setIntegerValue(int integerValue) {
		this.integerValue = integerValue;
	}
	public Character getCharacterValue() {
		return characterValue;
	}
	public void setCharacterValue(char characterValue) {
		this.characterValue = characterValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((booleanValue == null) ? 0 : booleanValue.hashCode());
		result = prime * result + ((byteValue == null) ? 0 : byteValue.hashCode());
		result = prime * result + ((characterValue == null) ? 0 : characterValue.hashCode());
		result = prime * result + ((doubleValue == null) ? 0 : doubleValue.hashCode());
		result = prime * result + ((floatValue == null) ? 0 : floatValue.hashCode());
		result = prime * result + ((integerValue == null) ? 0 : integerValue.hashCode());
		result = prime * result + ((longValue == null) ? 0 : longValue.hashCode());
		result = prime * result + ((shortValue == null) ? 0 : shortValue.hashCode());
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CsvLineObject other = (CsvLineObject) obj;
		if (booleanValue == null) {
			if (other.booleanValue != null)
				return false;
		} else if (!booleanValue.equals(other.booleanValue))
			return false;
		if (byteValue == null) {
			if (other.byteValue != null)
				return false;
		} else if (!byteValue.equals(other.byteValue))
			return false;
		if (characterValue == null) {
			if (other.characterValue != null)
				return false;
		} else if (!characterValue.equals(other.characterValue))
			return false;
		if (doubleValue == null) {
			if (other.doubleValue != null)
				return false;
		} else if (!doubleValue.equals(other.doubleValue))
			return false;
		if (floatValue == null) {
			if (other.floatValue != null)
				return false;
		} else if (!floatValue.equals(other.floatValue))
			return false;
		if (integerValue == null) {
			if (other.integerValue != null)
				return false;
		} else if (!integerValue.equals(other.integerValue))
			return false;
		if (longValue == null) {
			if (other.longValue != null)
				return false;
		} else if (!longValue.equals(other.longValue))
			return false;
		if (shortValue == null) {
			if (other.shortValue != null)
				return false;
		} else if (!shortValue.equals(other.shortValue))
			return false;
		if (stringValue == null) {
			if (other.stringValue != null)
				return false;
		} else if (!stringValue.equals(other.stringValue))
			return false;
		return true;
	}	
}
