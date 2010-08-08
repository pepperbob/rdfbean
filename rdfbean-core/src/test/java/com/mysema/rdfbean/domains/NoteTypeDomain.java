package com.mysema.rdfbean.domains;

import java.util.Set;

import com.mysema.rdfbean.TEST;
import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.model.ID;
import com.mysema.rdfbean.model.IDType;

public interface NoteTypeDomain {
    
    @ClassMapping(ns=TEST.NS)
    public static class Note {
        
        @Id(IDType.RESOURCE)
        public ID id;
        
        @Predicate
        public NoteType type;
        
        @Predicate
        public Set<NoteType> types;

        public ID getId() {
            return id;
        }

        public NoteType getType() {
            return type;
        }

        public Set<NoteType> getTypes() {
            return types;
        }
        
    }
    
    @ClassMapping(ns=TEST.NS)
    public enum NoteType {
        TYPE1,
        TYPE2
    }

}