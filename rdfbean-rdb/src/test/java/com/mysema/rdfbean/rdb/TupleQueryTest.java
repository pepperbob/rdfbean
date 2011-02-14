package com.mysema.rdfbean.rdb;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.mysema.query.types.Predicate;
import com.mysema.rdfbean.TEST;
import com.mysema.rdfbean.model.*;

public class TupleQueryTest extends AbstractConnectionTest{
    
    private static final QNODE<ID> subject = new QNODE<ID>(ID.class, "s");
    
    private static final QNODE<UID> predicate = new QNODE<UID>(UID.class, "p");
    
    private static final QNODE<NODE> object = new QNODE<NODE>(NODE.class, "o");

    private RDFQuery query(){
        return new RDFQueryImpl(connection);
    }    

    @Test
    public void Pattern(){
        query().where(Blocks.pattern(subject, RDF.type, RDFS.Class)).select(subject);
    }
    
    @Test
    public void Pattern_with_Filters(){
        Block pattern = Blocks.pattern(subject, predicate, object); 
        
        List<Predicate> filters = Arrays.<Predicate>asList(
                subject.eq(new UID(TEST.NS)),
                predicate.eq(RDFS.label),
                subject.ne(new UID(TEST.NS)),
                object.isNull(),
                object.isNotNull(),
                object.lit().lt("X"),
                object.lit().gt("X"),
                object.lit().loe("X"),
                object.lit().goe("X")
        );
        
        for (Predicate filter : filters){
            query().where(pattern, filter).select(subject);
        }
    }
    
    @Test
    public void Pattern_with_Limit_and_Offset(){
        query().where(Blocks.pattern(subject, RDF.type, RDFS.Class))
                .limit(5)
                .offset(20)
                .select(subject);
    }
    
    @Test
    public void Group(){
        query().where(
                Blocks.pattern(subject, RDF.type, RDFS.Class),
                Blocks.pattern(subject, predicate, object))
                .select(subject, predicate, object);
    }
    
    @Test
    @Ignore
    public void Union(){
        // FIXME
        query().where(
                Blocks.union(
                    Blocks.pattern(subject, RDF.type, RDFS.Class),
                    Blocks.pattern(subject, predicate, object)
                )).select(subject, predicate, object);
    }
    
    @Test
    public void Optional(){
        query().where(
                Blocks.pattern(subject, RDF.type, RDFS.Class),
                Blocks.optional(Blocks.pattern(subject, predicate, object)))
                .select(subject, predicate, object);
    }
}