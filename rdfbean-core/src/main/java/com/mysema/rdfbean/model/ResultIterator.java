/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.model;

import java.util.Iterator;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.mysema.commons.lang.CloseableIterator;

/**
 * @author tiwe
 */
public final class ResultIterator implements CloseableIterator<STMT> {

    private final Iterator<STMT> iter;

    ResultIterator(Iterator<STMT> iterator, @Nullable final ID subject, @Nullable final UID predicate,
            @Nullable final NODE object, @Nullable final UID context, final boolean includeInferred) {
        this.iter = Iterators.filter(iterator, new Predicate<STMT>() {
            @Override
            public boolean apply(STMT stmt) {
                return STMTMatcher.matches(stmt, subject, predicate, object, context, includeInferred);
            }
        });
    }

    @Override
    public void close() {
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public STMT next() {
        return iter.next();
    }

    @Override
    public void remove() {
    }

}