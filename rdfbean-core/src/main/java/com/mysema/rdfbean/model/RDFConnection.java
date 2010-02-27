/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.model;

import java.io.Closeable;
import java.util.Set;

import javax.annotation.Nullable;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.rdfbean.object.Session;

/**
 * RDFConnection defines a session interface to the Repository
 *
 * @author tiwe
 * @version $Id$
 *
 */
public interface RDFConnection extends Closeable{
    
    /**
     * Create a new transaction for the Connection
     * 
     * @param readOnly
     * @param txTimeout
     * @param isolationLevel
     * @return
     */
    RDFBeanTransaction beginTransaction(boolean readOnly, int txTimeout, int isolationLevel);

    /**
     * Clear any cached objects in the Connection
     */
    void clear();
    
    /**
     * Create a new unique Blank node
     * 
     * @return
     */
    BID createBNode();
    
    /**
     * Prepare a Query of the given query language with the given definition
     * 
     * @param <D>
     * @param <Q>
     * @param session
     * @param queryLanguage
     * @param definition
     * @return
     */
    <D,Q> Q createQuery(Session session, QueryLanguage<D,Q> queryLanguage, @Nullable D definition);
    
    /**
     * Find the statements matching the given pattern
     * 
     * @param subject
     * @param predicate
     * @param object
     * @param context
     * @param includeInferred true, if inferred triples are included, and false, if not
     * @return
     */
    CloseableIterator<STMT> findStatements(
            @Nullable ID subject, 
            @Nullable UID predicate, 
            @Nullable NODE object, 
            @Nullable UID context, boolean includeInferred);

    /**
     * Get a unallocated local id for use in a ID/LID mapping
     * 
     * @return
     */
    long getNextLocalId();
    
    /**
     * Update the Repository with the given statements
     * 
     * @param removedStatements statements to be removed
     * @param addedStatements statement to be added
     */
    void update(Set<STMT> removedStatements, Set<STMT> addedStatements);

}
