package com.group.pojo.sonar;

public class Period {

	private int index;
	private String value;
	private boolean bestValue;
	
	public Period(int index, String value, boolean bestValue) {
		this.index=index;
		this.value=value;
		this.bestValue=bestValue;
	}
	
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isBestValue() {
		return bestValue;
	}
	public void setBestValue(boolean bestValue) {
		this.bestValue = bestValue;
	}
	
}
