/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.model;

import javax.annotation.Nullable;

import net.jcip.annotations.Immutable;

import com.mysema.commons.lang.Assert;
import com.mysema.query.annotations.Entity;


/**
 * @author sasa
 *
 */
@Entity
@Immutable
public final class STMT {
    
    private final ID subject;
        
    private final UID predicate;
    
    private final NODE object;
    
    @Nullable
    private final UID context;
    
    private final boolean asserted;

    public STMT(ID subject, UID predicate, NODE object) {
        this(subject, predicate, object, null);
    }

    public STMT(ID subject, UID predicate, NODE object, @Nullable UID context) {
        this(subject, predicate, object, context, true);
    }

    public STMT(ID subject, UID predicate, NODE object, @Nullable UID context, boolean asserted) {
        this.subject = Assert.notNull(subject);
        this.predicate = Assert.notNull(predicate);
        this.object = Assert.notNull(object);
        this.context = context;
        this.asserted = asserted;
    }

    public NODE getObject() {
        return object;
    }

    public UID getPredicate() {
        return predicate;
    }

    public ID getSubject() {
        return subject;
    }
    
    public String toString() {
        return "" + subject + " " + predicate + " " + object;
    }

    public UID getContext() {
        return context;
    }

    public boolean isObjectStatement() {
        return object instanceof ID;
    }

    public boolean isAsserted() {
        return asserted;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + ((context == null) ? 0 : context.hashCode());
        result = prime * result + (asserted ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof STMT) {
            STMT other = (STMT) obj;
            
            if (!subject.equals(other.subject))
                return false;

            if (!predicate.equals(other.predicate))
                return false;
            
            if (!object.equals(other.object))
                return false;
            
            if (context == null) {
                if (other.context != null)
                    return false;
            } else if (!context.equals(other.context))
                return false;

            if (asserted != other.asserted)
                return false;

            return true;
        } else {
            return false;
        }
    }
    
}
