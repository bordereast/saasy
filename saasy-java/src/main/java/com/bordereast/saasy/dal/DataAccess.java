package com.bordereast.saasy.dal;

import java.util.Map;

import com.bordereast.saasy.domain.BaseEntity;
import com.bordereast.saasy.domain.JoinedEntity;
import com.bordereast.saasy.domain.PagedList;
import com.bordereast.saasy.exception.DataException;

public interface DataAccess {
       
	public <T extends BaseEntity> T getByKey(String key, Class<T> c) throws DataException;
	public <T extends BaseEntity> T update(T entity, Class<T> c) throws DataException;
	public <T extends BaseEntity> boolean delete(T entity, Class<T> c) throws DataException;
	public <T extends BaseEntity> T insert(T entity, Class<T> c) throws DataException;
	public boolean createCollection(String collectionName);
	public <T extends BaseEntity> T getSingleByAQL(String aql, Map<String, Object> bindVars, Class<T> c) throws DataException;
	public <T extends BaseEntity> PagedList<T> getPagedCollection(String collection, String cursorId, Class<T> c) throws DataException;
	public String getAql_ModuleSingleByParams();
	
	
	public <T extends JoinedEntity> T insert(T entity, Class<T> c) throws DataException;
    public <T extends JoinedEntity> T update(T entity, Class<T> c) throws DataException;
}
