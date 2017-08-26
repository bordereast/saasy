package com.bordereast.saasy.domain.menu;

import java.util.ArrayList;
import java.util.List;

import com.bordereast.saasy.domain.BaseEntity;

public class Menu extends BaseEntity {
	private int sortOrder;
	private String localeFile;
	private String localeTitle;
	private List<MenuItem> items = new ArrayList<MenuItem>();
	
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
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
	public List<MenuItem> getItems() {
		return items;
	}
	public void setItems(List<MenuItem> items) {
		this.items = items;
	}
	
	
}
