/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.object;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.apache.commons.collections15.BeanMap;

/**
 * @author sasa
 *
 */
public class FieldProperty extends MappedProperty<Field> {

	private Field field;

	public FieldProperty(Field field) {
		super(field.getName(), field.getAnnotations());
		this.field = field;
        this.field.setAccessible(true);
	}

	@Override
	public Field getMember() {
		return field;
	}

	@Override
	public Class<?> getType() {
		return field.getType();
	}

	@Override
	public void setValue(BeanMap beanMap, Object value) {
	    if (value == null && field.getType().isPrimitive()) {
	        return;
	    }
		try {
//            field.set(beanWrapper.getWrappedInstance(), value);
            field.set(beanMap.getBean(), value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
	}

    @Override
    public Object getValue(BeanMap instance) {
        try {
            return field.get(instance.getBean());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	protected Type getParametrizedType() {
		return field.getGenericType();
	}

    @Override
    public boolean isVirtual() {
        return false;
    }
	
}
