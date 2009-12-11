package com.mysema.rdfbean.lucene.conf;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.junit.Test;

import com.mysema.rdfbean.TEST;
import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.lucene.Searchable;
import com.mysema.rdfbean.lucene.SearchablePredicate;
import com.mysema.rdfbean.model.UID;
import com.mysema.rdfbean.object.DefaultConfiguration;

/**
 * InheritanceConfTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class InheritanceTest extends AbstractConfigurationTest{
    
    @ClassMapping(ns=TEST.NS)
    @Searchable
    public static class Animal{
        @Predicate
        @SearchablePredicate
        String name;
    }
    
    @ClassMapping(ns=TEST.NS)
    @Searchable
    public static class Cat extends Animal{
        @Predicate
        @SearchablePredicate
        Cat mate;
    }
    
    @ClassMapping(ns=TEST.NS)
    public static class Dog extends Animal{
        @Predicate
        int age;
    }
    
    @Test
    public void inheritance(){
        configuration.setCoreConfiguration(new DefaultConfiguration(Animal.class, Cat.class, Dog.class));
        configuration.initialize();
        
        // name needs to be handled everywhere, since it's declared as a searchable predicate
        // in Animal
        UID namePred = new UID(TEST.NS, "name");
        assertNotNull(configuration.getPropertyConfig(namePred, Collections.singleton(new UID(TEST.NS, Animal.class.getSimpleName()))));
        assertNotNull(configuration.getPropertyConfig(namePred, Collections.singleton(new UID(TEST.NS, Cat.class.getSimpleName()))));
        assertNotNull(configuration.getPropertyConfig(namePred, Collections.singleton(new UID(TEST.NS, Dog.class.getSimpleName()))));
        
        UID matePred = new UID(TEST.NS, "mate");
        assertNotNull(configuration.getPropertyConfig(matePred, Collections.singleton(new UID(TEST.NS, Cat.class.getSimpleName()))));
        
        UID agePred = new UID(TEST.NS, "age");
        assertNull(configuration.getPropertyConfig(agePred, Collections.singleton(new UID(TEST.NS, Dog.class.getSimpleName()))));
    }

}
