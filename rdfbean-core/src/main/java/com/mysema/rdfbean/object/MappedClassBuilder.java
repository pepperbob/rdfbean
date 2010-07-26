/**
 * 
 */
package com.mysema.rdfbean.object;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mysema.commons.lang.Assert;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.model.ID;
import com.mysema.rdfbean.model.IDType;
import com.mysema.rdfbean.model.UID;

/**
 * @author tiwe
 *
 */
public class MappedClassBuilder {

    private final Set<String> handled = new HashSet<String>();
    
    private final MappedClass mappedClass;
    
    private final String defaultNamespace;
    
    public MappedClassBuilder(MappedClass mappedClass) {
        this.mappedClass = Assert.notNull(mappedClass,"mappedClass");   
        this.defaultNamespace = mappedClass.getUID().getNamespace();
    }

    public MappedClassBuilder addId(String propertyName) {
        try {
            handled.add(propertyName);
            Field field = mappedClass.getJavaClass().getDeclaredField(propertyName);
            Id id;
            if (field.getType().equals(ID.class)){
                id = new IdImpl(IDType.RESOURCE);
            }else if (field.getType().equals(UID.class)){
                id = new IdImpl(IDType.URI);
            }else{
                id = new IdImpl(IDType.LOCAL);
            }
            return addMappedPath(field, Collections.<MappedPredicate>emptyList(),id);
        } catch (SecurityException e) {
            throw new ConfigurationException(e);
        } catch (NoSuchFieldException e) {
            throw new ConfigurationException(e);
        }            
    }
    
    private MappedClassBuilder addMappedPath(Field field, List<MappedPredicate> predicates, Annotation... annotations){
        FieldProperty fieldProperty = new FieldProperty(field, annotations, mappedClass);
        MappedPath mappedPath = new MappedPath(fieldProperty, predicates, false);
        mappedClass.addMappedPath(mappedPath);
        return this;
    }

    public MappedClassBuilder addProperties() {
        String ns = mappedClass.getUID().getNamespace();
        for (Field field : mappedClass.getJavaClass().getDeclaredFields()){
            if (!handled.contains(field.getName())){
                Predicate predicate = new PredicateImpl("",ns,field.getName(),false);
                MappedPredicate mappedPredicate = new MappedPredicate(ns, predicate, null);
                addMappedPath(field, Collections.singletonList(mappedPredicate), predicate);
            }       
        }
        return this;
    }

    public MappedClassBuilder addProperty(String propertyName) {
        return addProperty(propertyName, new UID(defaultNamespace, propertyName), false);        
    }    

    public MappedClassBuilder addProperty(String propertyName, UID uid) {
        return addProperty(propertyName, uid, false);
    }
    
    public MappedClassBuilder addProperty(String propertyName, UID uid, boolean inv) {
        try {
            handled.add(propertyName);
            Field field = mappedClass.getJavaClass().getDeclaredField(propertyName);
            Predicate predicate = new PredicateImpl("",uid.ns(),uid.ln(),inv);
            MappedPredicate mappedPredicate = new MappedPredicate(defaultNamespace, predicate, null);
            return addMappedPath(field, Collections.singletonList(mappedPredicate), predicate);
        } catch (SecurityException e) {
            throw new ConfigurationException(e);
        } catch (NoSuchFieldException e) {
            throw new ConfigurationException(e);
        }
    }
        
}