package com.bordereast.saasy.dal;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.velocypack.VPack;
import com.arangodb.velocypack.VPackParser;
import com.arangodb.velocypack.module.jdk8.VPackJdk8Module;
import com.bordereast.jaql.JAQL;
import com.bordereast.jaql.arango.Operator;
import com.bordereast.jaql.arango.annotation.ArangoRelation;
import com.bordereast.saasy.domain.BaseEntity;
import com.bordereast.saasy.domain.JoinedEntity;
import com.bordereast.saasy.domain.PagedList;
import com.bordereast.saasy.exception.DataException;
import com.bordereast.saasy.util.ReflectionUtil;
import com.google.gson.Gson;

import io.vertx.core.json.JsonObject;

public class DataAccessImpl implements DataAccess {

	private ArangoDB arangoDB; 
	private String database;
	private VPackParser parser; 
    private VPack vpack;
	
	public DataAccessImpl(String database) {
		this.database = database;
		arangoDB = ArangoBuilderService.getInstance().getArangoDB();
		parser = new VPackParser.Builder().build();
        vpack = new VPack.Builder().registerModule(new VPackJdk8Module()).build();
	}
	
	public boolean createCollection(String collectionName) {
	    CollectionEntity collection = arangoDB.db(database).createCollection(collectionName);
	    
	    return collection != null && collection.getName().equals(collectionName);
	}
	

	public <T extends BaseEntity> T getByKey(String key, Class<T> c) throws DataException {
		try {
		    
		    if(JoinedEntity.class.isAssignableFrom(c)) {
		        return getJoinedEntityByKey(key, c);
		    }
		    
			T value = arangoDB.db(database).collection(c.getSimpleName().toLowerCase()).getDocument(key, c);
			return value;
		} catch(ArangoDBException e){
			throw new DataException(e.getMessage(), e);
		}
	}	


    public <T extends BaseEntity> T insert(T entity, Class<T> c) throws DataException {
		try {
			//String key = ((BaseEntity)entity).getKey();
			DocumentCreateEntity<T> value = arangoDB.db(database).collection(c.getSimpleName().toLowerCase()).insertDocument(entity);
					
			return (T) value.getNew(); 
		} catch(ArangoDBException e){
			throw new DataException(e.getMessage(), e);
		}
	}
	
	public <T extends BaseEntity> T update(T entity, Class<T> c) throws DataException {
		try {
			String key = ((BaseEntity)entity).getKey();
			DocumentUpdateEntity<T> value = arangoDB.db(database).collection(c.getSimpleName().toLowerCase()).updateDocument(key, entity);
			return (T) value.getNew(); 
		} catch(ArangoDBException e){
			throw new DataException(e.getMessage(), e);
		}
	}
	
	   
    private <T extends BaseEntity> T getJoinedEntityByKey(String key, Class<T> c) {
        try {
            JAQL j = new JAQL();
            T n = c.newInstance();
            String query = j
                    .forEntity(n)
                    .filter(j.currentAlias() + "._key", Operator.equals, j.addParam("userkey", key))
                    .returnEntity(n)
                    .build();
            
            ArangoCursor<T> cursor = arangoDB.db(database).query(query, j.bindVars(), null, c);
            
            if(cursor.hasNext()) {
                return cursor.next();
            }
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return null;
    }

/*    private <T extends BaseEntity> String getArangoRelationQuery(String key, Class<T> c)
            throws InstantiationException, IllegalAccessException {

        String entityCollection = c.getSimpleName().toLowerCase();
        List<String> parts = new ArrayList<String>();
        
        
        for(Field field  : c.getDeclaredFields())
        {
            ArangoRelation tation = field.getAnnotation(ArangoRelation.class);
            if(tation != null) {
                String toField = tation.fieldTo();
                String targetCollection = tation.targetCollection();
                String fromField = tation.fieldFrom();
                ParameterizedType fieldListType = (ParameterizedType)field.getGenericType();
                Class<?> fieldListClass = (Class<?>) fieldListType.getActualTypeArguments()[0];

                String joinClass = fieldListClass.getSimpleName().toLowerCase();
                
                parts.add(buildJoinedEntityMergeQueryPart(toField, fromField, targetCollection, joinClass));
            }
        }
        
        String query = buildJoinedEntityQuery(parts, key, entityCollection);
        return query;
    }*/
    
    private String buildJoinedEntityQuery(List<String> parts, String key, String entityCollection) {
        StringBuilder str = new StringBuilder();
        
        str.append(String.format("FOR u in %s ", entityCollection));
        str.append(String.format("FILTER u._key == '%s' ", key));
        str.append("RETURN MERGE (u, { ");
        
        for(String s : parts) {
            str.append(s);
        }
        
        str.append("})");
        
        
        return str.toString();
    }
    
    private String buildJoinedEntityMergeQueryPart(String toField, String fromField, String targetCollection, String joinClass) {
        return String.format("%s: (FOR r IN %s FILTER r._key IN FLATTEN(FOR ur IN %s "
                + "FILTER ur._key == u._key RETURN ur.%s) return r)  ", fromField, joinClass, targetCollection, toField);

    }
    
	public <T extends JoinedEntity> T insert(T entity, Class<T> c) throws DataException {
        try {
            DocumentCreateEntity<T> value = arangoDB.db(database).collection(c.getSimpleName().toLowerCase()).insertDocument(entity);
            
            if(value != null && value.getKey() != null) {
                saveJoinedFields(entity, c);
            }
            
            return (T) value.getNew(); 
        } catch(ArangoDBException e){
            throw new DataException(e.getMessage(), e);
        }
	}
   
	@SuppressWarnings("unchecked")
    private <T extends JoinedEntity> void saveJoinedFields(T entity, Class<T> c) {
    	    try {
    	        String key = ((JoinedEntity)entity).getKey();
    	        Gson gson = new Gson();
    	        
                Map<String, List<BaseEntity>> values = new HashMap<String, List<BaseEntity>>();
                for(Field field  : c.getDeclaredFields())
                {
                    ArangoRelation tation = field.getAnnotation(ArangoRelation.class);
                    if(tation != null) {
                        String targetField = field.getName();
                        String targetCollection = tation.targetCollection();
                        
                        field.setAccessible(true);
                        List<BaseEntity> list = (List<BaseEntity>) field.get(entity);
                        
                        List<String> roles = list.stream().map(BaseEntity::getKey).collect(Collectors.toList());
                        String json = gson.toJson(roles); 
                        
                        String upsert = getUpsertStatement(targetCollection, key, targetField, json);

                        arangoDB.db(database).query(upsert, null, null, String.class);
                        
                        field.setAccessible(false);
                    }
                }
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
	    
	}
	
	private String getUpsertStatement(String targetCollection, String key, String fieldTo, String arrayStr) {
	    String stmt = "UPSERT { _key: '{1}' } " + 
	            "INSERT { _key: '{1}', {2}: {3} } " + 
	            "UPDATE { {2}: {3} } IN {0}";
	    
	    return String.format("UPSERT { _key: '%s' } ", key) + 
	            String.format("INSERT { _key: '%s', %s: %s } ", key, fieldTo, arrayStr) + 
	            String.format("UPDATE { %s: %s } IN %s", fieldTo, arrayStr, targetCollection);
	    
	    //return MessageFormat.format(stmt, targetCollection, key, fieldTo, arrayStr);

	}
	
	@SuppressWarnings("unchecked")
    public <T extends JoinedEntity> T update(T entity, Class<T> c) throws DataException {
        try {
            String key = ((JoinedEntity)entity).getKey();
                        
            Map<String, List<BaseEntity>> values = new HashMap<String, List<BaseEntity>>();
            for(Field field  : c.getDeclaredFields())
            {
                ArangoRelation tation = field.getAnnotation(ArangoRelation.class);
                if(tation != null) {
                    String targetField = field.getName();
                    field.setAccessible(true);
                    List<BaseEntity> list = (List<BaseEntity>) field.get(entity);
                    values.put(targetField, list);
                    field.setAccessible(false);
                }
            }

            
            
            // iterate over values   
            
            for (Map.Entry<String, List<BaseEntity>> entry : values.entrySet()) {
                
                JsonObject jsonObject = new JsonObject();
                List<String> strings = new ArrayList<String>();
                for(BaseEntity val : entry.getValue()) {
                    strings.add(val.getKey());
                }
                
                jsonObject.put("_key", entity.getKey() + "_" + entry.getKey().toLowerCase());
                jsonObject.put(entry.getKey(), strings);
                
                try {
                    arangoDB.db(database).collection(c.getSimpleName().toLowerCase() + "_" + entry.getKey().toLowerCase())
                        .deleteDocument(entity.getKey() + "_" + entry.getKey().toLowerCase());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                arangoDB.db(database).collection(c.getSimpleName().toLowerCase() + "_" + entry.getKey().toLowerCase())
                    .insertDocument(jsonObject.toString());
            }
            
            

            DocumentUpdateEntity<T> value = arangoDB.db(database).collection(c.getSimpleName().toLowerCase()).updateDocument(key, entity);

            return value.getNew(); 
        } catch(Exception e){
            throw new DataException(e.getMessage(), e);
        }
    }
	
	
	
	
	public <T extends BaseEntity> boolean delete(T entity, Class<T> c) throws DataException {
		try {
			String key = ((BaseEntity)entity).getKey();
			arangoDB.db(database).collection(c.getSimpleName().toLowerCase()).deleteDocument(key);
			return true;
		} catch(ArangoDBException e){
			throw new DataException(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends BaseEntity> T getSingleByAQL(String aql, Map<String, Object> bindVars, Class<T> c) throws DataException {
	    try {
	        AqlQueryOptions options = new AqlQueryOptions();
	        options.batchSize(1);
	        ArangoCursor<T> cursor = arangoDB.db(database).query(aql, bindVars, options, c);
	        return (T) cursor.next();
	    } catch(ArangoDBException e){
            throw new DataException(e.getMessage(), e);
        }
	}
	

	public <T extends BaseEntity> PagedList<T> getPagedCollection(String collection, String cursorId, Class<T> c) throws DataException {
	    
	    ArangoCursor<T> cursor;
	    
	    if(cursorId != null) {
	        cursor = arangoDB.db(database).cursor(cursorId, c);
	    } else {
	        Map<String, Object> bindVars = new HashMap<String, Object>();
	        bindVars.put("@collection", collection);
	        AqlQueryOptions options = new AqlQueryOptions();
	        options.batchSize(10);
	        options.count(true);
	        options.cache(true);
	        cursor = arangoDB.db(database).query("FOR x IN @@collection RETURN x", bindVars, options, c);
	    }
	    
	    if(cursor == null) return null;
	    

	    List<T> list = new ArrayList<T>();
	    
	    while(cursor.hasNext()) {
	        list.add((T)cursor.next());
	    }
	    
	    PagedList<T> pagedList = new PagedList<T>(list, cursor.getCount(), cursor.getId());
	    
	    return pagedList;
	}

    @Override
    public String getAql_ModuleSingleByParams() {
        final String query = "for x in module " + 
                "filter x._key == @mod " + 
                "return MERGE ( x, { components: ( " + 
                "        for c in x.components filter c.name == @comp " + 
                "        return MERGE (c, {actions: ( " + 
                "            for a in c.actions filter a.name == @action return a " + 
                "        )}))})";
        return query;
    }
	
}
