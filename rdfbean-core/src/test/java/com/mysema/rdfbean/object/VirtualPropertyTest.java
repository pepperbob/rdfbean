package com.mysema.rdfbean.object;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.mysema.rdfbean.TEST;
import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.InjectProperty;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.model.MiniRepository;
import com.mysema.rdfbean.model.STMT;
import com.mysema.rdfbean.model.UID;

public class VirtualPropertyTest {
    
    @ClassMapping(ns=TEST.NS)
    public static interface Party {

        @Predicate
        String getDisplayName();

    }
    
    @ClassMapping(ns=TEST.NS)
    public static class Person implements Party {
        
        @Predicate
        String firstName;

        @Predicate
        String lastName;

        public Person(
                @InjectProperty("firstName")
                String firstName, 
                @InjectProperty("lastName")
                String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public String getDisplayName() {
            return firstName + " " + lastName;
        }
        
    }
    
    @Test
    public void testVirtualProperties() {
        MiniRepository repository = new MiniRepository();
        MiniSession session = new MiniSession(repository, Party.class, Person.class);
        
        // Persistence
        session.save(new Person("John", "Doe"));
        List<STMT> statements = repository.findStatements(null, new UID(TEST.NS, "displayName"), null);
        assertEquals(1, statements.size());
        STMT stmt = statements.get(0);
        assertEquals("John Doe", stmt.getObject().getValue());
        
        // Retrieval
        session = new MiniSession(repository, Party.class, Person.class);
        Person person = session.findInstances(Person.class).get(0);
        assertEquals("John Doe", person.getDisplayName());
    }

}