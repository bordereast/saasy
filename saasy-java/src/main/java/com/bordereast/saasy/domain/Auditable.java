package com.bordereast.saasy.domain;

import java.time.Instant;

public class Auditable {
    

	/* Audit Columns */
	private Instant modifiedOn;
	private Instant createdOn;
	
	private String modifiedBy;
	private String createdBy;
	
	private String createdFunc;
	private String modifiedFunc;
	
	public Instant getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Instant modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public Instant getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Instant createdOn) {
		this.createdOn = createdOn;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedFunc() {
		return createdFunc;
	}
	public void setCreatedFunc(String createdFunc) {
		this.createdFunc = createdFunc;
	}
	public String getModifiedFunc() {
		return modifiedFunc;
	}
	public void setModifiedFunc(String modifiedFunc) {
		this.modifiedFunc = modifiedFunc;
	}
}
