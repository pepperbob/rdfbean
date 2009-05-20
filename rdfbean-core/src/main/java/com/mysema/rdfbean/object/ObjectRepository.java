/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.object;

import com.mysema.rdfbean.model.UID;


/**
 * Bean container that is able to return an instance of given class with given id.
 * 
 * @author sasa
 */
public interface ObjectRepository {

    /**
     * Return an instance of clazz with given id (URI).
     * 
     * @param clazz     type requested. May be null.
     * @param uri       id if the requested resource. Never null.
     * @return          requested instance or null if not found.
     */
    <T> T getBean(Class<T> clazz, UID uri);

}
