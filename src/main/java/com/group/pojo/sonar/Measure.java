package com.group.pojo.sonar;

import com.group.pojo.sonar.Period;

import java.util.ArrayList;

public class Measure {

	private String metric;
	private String value;
	private ArrayList<Period> periods;
	private boolean bestValue;
	
	public Measure(String metric, String value, boolean bestValue) {
		this.metric = metric;
		this.value = value;
		this.periods = new ArrayList<Period>();
		this.bestValue = bestValue;
	}
	
	
	public Measure(String metric, String value, ArrayList<Period> periods, boolean bestValue) {
		this.metric = metric;
		this.value = value;
		this.periods = periods;
		this.bestValue = bestValue;
	}
	
	public String getMetric() {
		return metric;
	}
	public void setMetric(String metric) {
		this.metric = metric;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public ArrayList<Period> getPeriods() {
		return periods;
	}
	public void setPeriods(ArrayList<Period> periods) {
		this.periods = periods;
	}
	public boolean isBestValue() {
		return bestValue;
	}
	public void setBestValue(boolean bestValue) {
		this.bestValue = bestValue;
	}
}
