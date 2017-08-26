package com.bordereast.saasy.domain;

import java.util.List;

public class PagedList <T> {

    private List<T> results;
    private int count;
    private String cursor;
    
    public PagedList() {}
    public PagedList(List<T> results, int count, String cursor) {
        this.results = results;
        this.count = count;
        this.cursor = cursor;
    }

    public List<T> getResults() {
        return results;
    }
    public void setResults(List<T> results) {
        this.results = results;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public String getCursor() {
        return cursor;
    }
    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}
