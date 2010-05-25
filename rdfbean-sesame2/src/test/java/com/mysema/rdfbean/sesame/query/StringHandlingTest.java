/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.sesame.query;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.store.StoreException;

import com.mysema.query.types.expr.EBoolean;
import com.mysema.query.types.path.PString;
import com.mysema.rdfbean.object.BeanQuery;
import com.mysema.rdfbean.sesame.SessionTestBase;


/**
 * StringHandlingTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class StringHandlingTest extends SessionTestBase{
    
    private PString stringPath = var.listProperty.get(1).directProperty;
    

    @Before
    public void setUp() throws StoreException{
        session = createSession(FI, SimpleType.class, SimpleType2.class);
    }

    private BeanQuery where(EBoolean... conditions) {
        return from(var).where(conditions);
    }
    
    @Test
    public void untypedLiterals(){
        assertEquals(1, where(var.directProperty.eq("metaonto_elements")).count());
    }
    
    @Test
    public void matches(){
        assertEquals(1, where(var.directProperty.matches("propertym.*")).count());
        assertEquals(1, where(var.directProperty.matches(".*opertym.*")).count());
    }
    
    @Test
    public void typedLiterals(){
        assertEquals(1, where(var.directProperty.eq("propertymap")).count());
    }
    
    @Test
    public void startsWith(){
        assertEquals(1, where(stringPath.startsWith("ns")).count());     
    }
    
    @Test
    public void startsWithIgnoreCase(){
        assertEquals(1, where(stringPath.startsWith("NS",false)).count());   
    }
    
    @Test
    public void endsWith(){
        assertEquals(1, where(stringPath.endsWith("ix")).count());    
    }
    
    @Test
    public void endsWithIgnoreCase(){
        assertEquals(1, where(stringPath.endsWith("IX",false)).count());   
    }
    
    @Test
    public void contains(){
        assertEquals(2, where(stringPath.contains("i")).count());
    }
    
    @Test
    @Ignore
    public void in(){
     // Sesame doesn't support RDFS Datatype inferencing rules properly
        assertEquals(1, where(stringPath.in("metaonto_elements","B","C")).count());
    }
    
}