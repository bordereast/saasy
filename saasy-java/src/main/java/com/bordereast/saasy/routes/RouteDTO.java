package com.bordereast.saasy.routes;

import java.util.List;
import java.util.Map;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;

public class RouteDTO {
	private String tenant;
	private String lang; 
	private String module; 
	private String component; 
	private String action; 
	private String data;
	private Future<String> future;
	private String requestId;
	private HttpMethod httpMethod;
	private Map<String, List<String>> postVars;
	private boolean authorized;
	
	public RouteDTO(String tenant, String lang, String module, String component, String action, 
	                String data, String requestid, HttpMethod httpMethod, Map<String, List<String>> postVars, boolean authorized,
	                Future<String> future){
		this.tenant = tenant;
		this.lang = lang;
		this.module = module;
		this.component = component;
		this.action = action;
		this.data = data;
		this.future = future;
		this.requestId = requestid;
		this.httpMethod = httpMethod;
		this.postVars = postVars;
		this.authorized = authorized;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Future<String> getFuture() {
		return future;
	}

	public void setFuture(Future<String> future) {
		this.future = future;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, List<String>> getPostVars() {
        return postVars;
    }

    public void setPostVars(Map<String, List<String>> postVars) {
        this.postVars = postVars;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
