package com.group.pojo.sonar;

import java.util.ArrayList;

public class BaseComponent {

	private String id;
	private String key;
	private String name;
	private String qualifier;
	private ArrayList<Measure> measures;
	
	public BaseComponent(String id, String key, String name, String qualifier, ArrayList<Measure> measures) {
		this.id=id;
		this.key=key;
		this.name=name;
		this.qualifier=qualifier;
		this.measures=measures;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	public ArrayList<Measure> getMeasures() {
		return measures;
	}
	public void setMeasures(ArrayList<Measure> measures) {
		this.measures = measures;
	}

}
