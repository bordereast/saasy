package com.bordereast.saasy.content;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class TemplateDTO {

	private PebbleTemplate template;
	private String contentPath;
	
	public TemplateDTO(){}

	public PebbleTemplate getTemplate() {
		return template;
	}

	public void setTemplate(PebbleTemplate template) {
		this.template = template;
	}

	public String getContentPath() {
		return contentPath;
	}

	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}
	
}
