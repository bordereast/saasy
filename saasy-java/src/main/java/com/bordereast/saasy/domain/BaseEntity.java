package com.bordereast.saasy.domain;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

public class BaseEntity extends Auditable {

	@DocumentField(Type.KEY)
	private String key;
	
	
/*	@DocumentField(Type.TO)
	private String toId;
	
	@DocumentField(Type.FROM)
	private String fromId;*/
	

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

/*	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}*/
/*	public String getToId() {
		return toId;
	}
	public void setToId(String toId) {
		this.toId = toId;
	}
	public String getFromId() {
		return fromId;
	}
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}*/

/*	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}*/
	
}
