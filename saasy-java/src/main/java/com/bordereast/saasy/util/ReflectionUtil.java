package com.bordereast.saasy.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.bordereast.jaql.arango.annotation.ArangoRelation;


public class ReflectionUtil {

    private ReflectionUtil() {}

    public static List<Field> getFieldsAnnotatedWith(Field[] declaredFields) {
        List<Field> fieldList = new ArrayList<Field>();
        
        for(Field field  : declaredFields)
        {
            ArangoRelation tation = field.getAnnotation(ArangoRelation.class);
            if(tation != null) {
                fieldList.add(field);
            }
        }
        
        return fieldList;
    }


}
