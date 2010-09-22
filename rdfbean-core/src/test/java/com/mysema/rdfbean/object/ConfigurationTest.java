/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.rdfbean.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import com.mysema.query.Projectable;
import com.mysema.query.Query;
import com.mysema.query.types.Expression;
import com.mysema.rdfbean.CORE;
import com.mysema.rdfbean.owl.OWL;
import com.mysema.rdfbean.owl.TypedList;

/**
 * ConfigurationTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class ConfigurationTest {
    
    @Test(expected=IllegalArgumentException.class)
    public void invalidPackage(){
        new DefaultConfiguration(ConfigurationTest.class.getPackage());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void invalidClass(){
        new DefaultConfiguration(ConfigurationTest.class);
    }
    
    @Test
    public void scanPackages() throws IOException, ClassNotFoundException{
        DefaultConfiguration conf1 = new DefaultConfiguration();
        conf1.addPackages(OWL.class.getPackage());
        conf1.addClasses(TypedList.class);
        
        DefaultConfiguration conf2 = new DefaultConfiguration();
        conf2.scanPackages(OWL.class.getPackage());
        assertEquals(conf1.getMappedClasses(), conf2.getMappedClasses());
    }
    
    @Test
    public void scanPackages_from_file() throws IOException, ClassNotFoundException{
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DefaultConfiguration conf = new DefaultConfiguration();
        Set<Class<?>> classes = conf.scanPackage(classLoader, CORE.class.getPackage());
        assertFalse(classes.isEmpty()); 
        assertTrue(classes.contains(OWL.class));
    }
    
    @Test
    public void scanPackages_from_jar() throws IOException, ClassNotFoundException{
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DefaultConfiguration conf = new DefaultConfiguration();
        Set<Class<?>> classes = conf.scanPackage(classLoader, Query.class.getPackage());
        assertTrue(classes.contains(Projectable.class));
        assertTrue(classes.contains(Expression.class));
        assertFalse(classes.isEmpty()); 
    }

}
