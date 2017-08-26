package com.bordereast.saasy.dal;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.bordereast.jaql.arango.annotation.ArangoRelation;
import com.bordereast.saasy.domain.BaseEntity;
import com.bordereast.saasy.domain.JoinedEntity;
import com.bordereast.saasy.util.ReflectionUtil;

public class  ArangoQueryBuilder {

    private String[] markers = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","aa","bb","cc","dd","ee","ff","gg","hh","ii","jj","kk","ll","mm","nn","oo","pp","qq","rr","ss","tt","uu","vv","ww","xx","yy","zz"};
    private int index = 0;
    private String query;
    private Class<? extends Object> entityClass;
    private List<String> filterFields;
    private List<String> filterValues;
    
    public ArangoQueryBuilder() {
        filterFields = new ArrayList<String>();
        filterValues = new ArrayList<String>();
    }
    
    public String getQuery() throws InstantiationException, IllegalAccessException {
        query = buildLinkedRelationQuery(entityClass, markers[index]);
        return query;
    }
    
    public ArangoQueryBuilder filterBy(String field, String value) {
        filterFields.add(field);
        filterValues.add(value);
        return this;
    }
    
    public <T extends Object> ArangoQueryBuilder forEntity(T c) throws InstantiationException, IllegalAccessException {
        entityClass = c.getClass();
        return this;
    }
    
    
    /* OLD STUFF */
    
    private <T extends Object> String buildLinkedRelationParts(Class<? extends Object> c, String currentRef) throws InstantiationException, IllegalAccessException {
        
        return null;
    }
    

    private <T extends Object> String buildLinkedRelationQuery(Class<? extends Object> c, String currentRef) throws InstantiationException, IllegalAccessException {
      

        String entityCollection = c.getSimpleName().toLowerCase();
        List<String> parts = new ArrayList<String>();
        
        
        for(Field field  : c.getDeclaredFields())
        {
            ArangoRelation relation = field.getAnnotation(ArangoRelation.class);
            if(relation != null) {
                
                ParameterizedType fieldListType = (ParameterizedType)field.getGenericType();
                Class<?> fieldListClass = (Class<?>) fieldListType.getActualTypeArguments()[0];
                String joinClass = fieldListClass.getSimpleName().toLowerCase();
                
                if(JoinedEntity.class.isAssignableFrom(fieldListClass)) {
                    parts.add(buildJoinedEntityMergeQueryPart(fieldListClass, relation, joinClass, currentRef));
                } else {
                    parts.add(buildJoinedEntityMergeQueryPart(relation, joinClass, currentRef));
                }
                
            }
        }
        
       
        return buildJoinedEntityQuery(parts, entityCollection, currentRef);
    }

    private String buildJoinedEntityQuery(List<String> parts, String entityCollection, String currentRef) {
        StringBuilder str = new StringBuilder();
        
        str.append(String.format("FOR %s in %s ", currentRef, entityCollection));
        
      //  str.append(String.format("FILTER %s._key == '%s' ", currentRef, key));
        
        str.append(String.format("RETURN MERGE (%s, { ", currentRef));
        
        for(String s : parts) {
            str.append(s);
        }
        
        str.append("})");
        
        
        return str.toString();
    }
    
    private String buildJoinedEntityMergeQueryPart(ArangoRelation relation, String joinClass, String parentRef) {
        String thisRef = markers[index++];
        String innerRef = markers[index++];
        return String.format("%s: (FOR %s IN %s FILTER %s._key IN FLATTEN(FOR %s IN %s ", "", thisRef, joinClass, thisRef, innerRef, relation.targetCollection()) +
                String.format("FILTER %s._key == %s._key RETURN %s.%s) return %s)  ", innerRef, parentRef, innerRef, "", thisRef);
    }
    
    private String buildJoinedEntityMergeQueryPart(
            Class<?> fieldListClass, ArangoRelation relation, String joinClass, String parentRef) throws InstantiationException, IllegalAccessException {
        String thisRef = markers[index++];
        String innerRef = markers[index++];
        String innerField = "";
        String innerQuery = buildLinkedRelationQuery(fieldListClass, thisRef);
        return String.format("%s: (FOR %s IN %s FILTER %s._key IN FLATTEN(FOR %s IN %s ", "", thisRef, joinClass, thisRef, innerRef, relation.targetCollection()) +
                String.format("FILTER %s._key == %s._key RETURN %s.%s) return MERGE(%s, {%s: %s}))  ", innerRef, parentRef, innerRef, "", thisRef, innerField, innerQuery);
    }

}
