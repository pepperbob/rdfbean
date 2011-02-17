/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Nullable;

/**
 * MultiIterator provides a cartesian view on the given iterators
 *
 * @author tiwe
 */
public class MultiIterator<T> implements Iterator<T> {

    @Nullable
    private Boolean hasNext;

    private int index = 0;

    private final List<? extends Iterable<T>> iterables;

    private final List<Iterator<T>> iterators;

    private final boolean[] lastEntry;

    private T last;

    public MultiIterator(List<? extends Iterable<T>> iterables){
        this.iterables = iterables;
        this.iterators = new ArrayList<Iterator<T>>(iterables.size());
        for (int i = 0; i < iterables.size(); i++){
            iterators.add(null);
        }
        this.lastEntry = new boolean[iterables.size()];
    }

    @Override
    public boolean hasNext() {
        while (hasNext == null) {
            produceNext();
        }
        return hasNext.booleanValue();
    }

    @Override
    public T next() {
        while (hasNext == null) {
            produceNext();
        }
        if (hasNext.booleanValue()) {
            hasNext = null;
            return last;
        } else {
            throw new NoSuchElementException();
        }
    }

    private void produceNext() {
        for (int i = index; i < iterables.size(); i++) {
            if (iterators.get(i) == null || (!iterators.get(i).hasNext() && i > 0)) {
//                System.err.println("c"+i);
                iterators.set(i, iterables.get(i).iterator());
            }
            if (!iterators.get(i).hasNext()) {
                hasNext = i == 0 ? Boolean.FALSE : null;
                return;
            }
//            System.err.println("n"+i);
            T value = iterators.get(i).next();
            if (i == iterables.size() -1){
                last = value;
            }
            lastEntry[i] = !iterators.get(i).hasNext();
            hasNext = Boolean.TRUE;
        }
        index = iterables.size() - 1;
        while (lastEntry[index] && index > 0){
            index--;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
