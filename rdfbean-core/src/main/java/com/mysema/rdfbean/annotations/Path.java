/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sasa
 *
 */
@Target( { METHOD, FIELD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {

	boolean ignoreInvalid() default false;
	
	String ns() default "";

	Predicate[] value();
	
}