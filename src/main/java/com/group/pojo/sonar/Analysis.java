package com.group.pojo.sonar;


import java.util.ArrayList;

public class Analysis {
	
	private Paging paging;
	private BaseComponent baseComponent;
	private ArrayList<Component> components;
	
	public Analysis(Paging paging, BaseComponent baseComponent, ArrayList<Component> components) {
		this.paging = paging;
		this.baseComponent = baseComponent;
		this.components = components;
	}
	
	public Analysis(Paging paging, BaseComponent baseComponent) {
		this.paging = paging;
		this.baseComponent = baseComponent;
		this.components = new ArrayList<Component>();
	}
	
	public Paging getPaging() {
		return paging;
	}
	public void setPaging(Paging paging) {
		this.paging = paging;
	}
	public BaseComponent getBaseComponent() {
		return baseComponent;
	}
	public void setBaseComponent(BaseComponent baseComponent) {
		this.baseComponent = baseComponent;
	}
	public ArrayList<Component> getComponents() {
		return components;
	}
	public void setComponents(ArrayList<Component> components) {
		this.components = components;
	}
	

}
