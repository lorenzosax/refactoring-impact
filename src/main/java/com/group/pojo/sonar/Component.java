package com.group.pojo.sonar;

import java.util.ArrayList;

public class Component {
	
	private String id;
	private String key;
	private String name;
	private String qualifier;
	private String path;
	private String language;
	private ArrayList<Measure> measures;
	
	public Component(String id, String key, String name, String qualifier, String path, String language, ArrayList<Measure> measures) {
		this.id = id;
		this.name = name;
		this.key=key;
		this.qualifier=qualifier;
		this.path=path;
		this.language=language;
		this.measures=measures;
	}
	
	public Component(String id, String key, String name, String qualifier, String path, String language) {
		this.id = id;
		this.name = name;
		this.key=key;
		this.qualifier=qualifier;
		this.path=path;
		this.language=language;
		this.measures=new ArrayList<Measure>();
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public ArrayList<Measure> getMeasures() {
		return measures;
	}
	public void setMeasures(ArrayList<Measure> measures) {
		this.measures = measures;
	}

	@Override
	public String toString() {
		return "Component [id=" + id + ", key=" + key + ", name=" + name + ", qualifier=" + qualifier + ", path=" + path
				+ ", language=" + language + ", measures=" + measures + "]";
	}

}
