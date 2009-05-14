/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.object;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author sasa
 *
 */
public class MethodProperty extends MappedProperty<Method> {

	public static MethodProperty getMethodPropertyOrNull(Method method) {
	    try {
	        return new MethodProperty(method);
	    } catch (IllegalArgumentException e) {
	        return null;
	    }
	}

	public static String getPropertyName(Method method) {
		String name = method.getName();
		Class<?> returnType = method.getReturnType();
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (name.startsWith("is")
				&& parameterTypes.length == 0 
				&& ( returnType.equals(boolean.class) 
						|| returnType.equals(Boolean.class) )) {
			return Character.toString(Character.toLowerCase(name.charAt(2))) + name.substring(3); 
		} else if (name.startsWith("get") 
				&& parameterTypes.length == 0 
				&& !returnType.equals(void.class)) {
			return Character.toString(Character.toLowerCase(name.charAt(3))) + name.substring(4);
		} else if (name.startsWith("set") // allow method chaining by returning "this"
				&& parameterTypes.length == 1) {
			return Character.toString(Character.toLowerCase(name.charAt(3))) + name.substring(4);
		} else {
			throw new IllegalArgumentException("Not getter or setter method: " + method);
		}
	}
	
	private boolean getter;
	
	private Method method;

	private MethodProperty(Method method) {
		super(getPropertyName(method), method.getAnnotations());
		this.method = method;
		if (method.getName().startsWith("set")) {
			getter = false;
		} else {
			getter = true;
		}
	}

	@Override
	public Method getMember() {
		return method;
	}

	@Override
	public Class<?> getType() {
		if (getter) {
			return method.getReturnType();
		} else {
			return method.getParameterTypes()[0];
		}
	}

	@Override
	public void setValue(BeanWrapper beanWrapper, Object value) {
		beanWrapper.setPropertyValue(getName(), value);
	}

    @Override
    public Object getValue(Object instance) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(instance);
        return beanWrapper.getPropertyValue(getName());
    }

	@Override
	protected Type getParametrizedType() {
		Type gtype = null;
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 1) {
			Type[] ptypes = method.getGenericParameterTypes();
			if (ptypes.length > 0) {
				gtype = ptypes[0];
			}
		} else {
			gtype = method.getGenericReturnType();
		}
		return gtype;
	}

    @Override
    public boolean isVirtual() {
        return getSetter() == null;
    }
    
    private Method getSetter() {
        Method setter = null;
        if (!getter) {
            setter = method;
        } else {
            Class<?> clazz = method.getDeclaringClass();
            Class<?> type = getType();
            if (boolean.class.equals(type) || Boolean.class.equals(type)) {
                try {
                    setter = clazz.getDeclaredMethod("is" + capitalize(getName()), type);
                } catch (SecurityException e) {
                    // ignore
                } catch (NoSuchMethodException e) {
                    // ignore
                }
            }
            if (setter == null) {
                try {
                    setter = clazz.getDeclaredMethod("get" + capitalize(getName()), type);
                } catch (SecurityException e) {
                    // ignore
                } catch (NoSuchMethodException e) {
                    // ignore
                }
            }
        }
        return setter;
    }

    private String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}