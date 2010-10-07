/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.model.io;


/**
 * Format provides
 *
 * @author tiwe
 * @version $Id$
 */
public enum Format {
    /**
     * 
     */
    N3("application/n3"),
    /**
     * 
     */
    NTRIPLES("text/plain"),
    /**
     * 
     */
    RDFA("application/xhtml+xml"),
    /**
     * 
     */
    RDFXML("application/rdf+xml"),
    /**
     * 
     */
    TRIG("application/x-trig"),
    /**
     * 
     */
    TURTLE("application/x-turtle"); 
    
    private final String mimetype;
    
    private Format(String mime){
        this.mimetype = mime;
    }
    
    public String getMimetype(){
        return mimetype;
    }

}
