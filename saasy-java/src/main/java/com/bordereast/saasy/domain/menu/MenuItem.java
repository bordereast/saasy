package com.bordereast.saasy.domain.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {
	private String name;
	private String href;
	private String localeFile;
	private String localeTitle;
	private String rel;
	private int sortOrder;
	private List<MenuItem> children = new ArrayList<MenuItem>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getLocaleFile() {
		return localeFile;
	}
	public void setLocaleFile(String localeFile) {
		this.localeFile = localeFile;
	}
	public String getLocaleTitle() {
		return localeTitle;
	}
	public void setLocaleTitle(String localeTitle) {
		this.localeTitle = localeTitle;
	}
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	public List<MenuItem> getChildren() {
		return children;
	}
	public void setChildren(List<MenuItem> children) {
		this.children = children;
	}
	
	
}
