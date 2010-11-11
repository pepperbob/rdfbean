package com.mysema.rdfbean.sesame.query;

import static com.mysema.query.alias.Alias.$;
import static com.mysema.query.alias.Alias.alias;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.StatementPattern;

import com.mysema.rdfbean.TEST;
import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Context;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.model.RDF;
import com.mysema.rdfbean.sesame.SessionTestBase;
import com.mysema.rdfbean.testutil.SessionConfig;

@SessionConfig({ContextTest.Entity1.class, ContextTest.Entity2.class, ContextTest.Entity3.class})
public class ContextTest extends SessionTestBase{

    private static final String NS1 = "http://www.example.com/ns1#";

    private static final String NS2 = "http://www.example.com/ns2#";

    private static final String NS3 = "http://www.example.com/ns3#";

    @ClassMapping(ns=TEST.NS)
    @Context(NS1)
    public static class Entity1 {

        @Id
        String id;

        @Predicate
        String property;

        @Predicate
        Entity2 entity;

        public String getProperty() {
            return property;
        }

        public Entity2 getEntity() {
            return entity;
        }

    }

    @ClassMapping(ns=TEST.NS)
    public static class Entity2 {

        @Id
        String id;

        @Predicate(context=NS2)
        String property;

        @Predicate
        Entity3 entity;

        public String getProperty() {
            return property;
        }

        public Entity3 getEntity() {
            return entity;
        }

    }

    @ClassMapping(ns=TEST.NS)
    public static class Entity3 {

        @Id
        String id;

        @Predicate(context=NS3)
        String property;

        @Predicate
        Entity1 entity;

        public String getProperty() {
            return property;
        }

        public Entity1 getEntity() {
            return entity;
        }

    }

    private static final Entity1 e1 = alias(Entity1.class);

    private static final Entity2 e2 = alias(Entity2.class);

    private static final Entity3 e3 = alias(Entity3.class);

    @Before
    public void setUp(){
        Entity1 entity1 = new Entity1();
        entity1.property = "X";
        Entity2 entity2 = new Entity2();
        entity2.property = "X";
        Entity3 entity3 = new Entity3();
        entity3.property = "X";
        session.save(entity1);
        session.save(entity2);
        session.save(entity3);
        session.flush();
    }

    @Test
    public void Counts(){
        assertEquals(1, session.from($(e1)).where($(e1.getProperty()).isNotNull()).list($(e1)).size());
        assertEquals(1, session.from($(e2)).where($(e2.getProperty()).isNotNull()).list($(e2)).size());
        assertEquals(1, session.from($(e3)).where($(e3.getProperty()).isNotNull()).list($(e3)).size());
    }

    @Test
    public void Entity1_property(){
        SesameQuery query = (SesameQuery) session.from($(e1)).where($(e1.getProperty()).isNotNull());
        query.list($(e1));
        Join join = (Join) query.getJoinBuilder().getTupleExpr();

        // rdf:type
        StatementPattern rdf_type = (StatementPattern) join.getArg(0);
        assertEquals(RDF.type.getId(), rdf_type.getPredicateVar().getValue().stringValue());
        assertEquals(NS1, rdf_type.getContextVar().getValue().stringValue());

        // property
        StatementPattern property = (StatementPattern) join.getArg(1);
        assertEquals(TEST.NS + "property", property.getPredicateVar().getValue().stringValue());
        assertEquals(NS1, property.getContextVar().getValue().stringValue());
    }

    @Test
    public void Entity1_entity(){
        SesameQuery query = (SesameQuery) session.from($(e1)).where(
                $(e1.getEntity()).isNotNull(),
                $(e1.getEntity().getProperty()).isNotNull());
        query.list($(e1));
        Join join = (Join) query.getJoinBuilder().getTupleExpr();

        // entity
        StatementPattern entity = (StatementPattern) ((Join) join.getArg(0)).getArg(1);
        assertEquals(TEST.NS + "entity", entity.getPredicateVar().getValue().stringValue());
        assertEquals(NS1, entity.getContextVar().getValue().stringValue());

        // entity.property
        StatementPattern property = (StatementPattern) join.getArg(1);
        assertEquals(TEST.NS + "property", property.getPredicateVar().getValue().stringValue());
        assertEquals(NS2, property.getContextVar().getValue().stringValue());
    }

    @Test
    public void Entity2_property(){
        SesameQuery query = (SesameQuery) session.from($(e2)).where($(e2.getProperty()).isNotNull());
        query.list($(e2));
        Join join = (Join) query.getJoinBuilder().getTupleExpr();

        // rdf:type
        StatementPattern rdf_type = (StatementPattern) join.getArg(0);
        assertEquals(RDF.type.getId(), rdf_type.getPredicateVar().getValue().stringValue());
        assertNull(rdf_type.getContextVar());

        // property
        StatementPattern property = (StatementPattern) join.getArg(1);
        assertEquals(TEST.NS + "property", property.getPredicateVar().getValue().stringValue());
        assertEquals(NS2, property.getContextVar().getValue().stringValue());

    }

    @Test
    public void Entity2_entity(){
        SesameQuery query = (SesameQuery) session.from($(e2)).where(
                $(e2.getEntity()).isNotNull(),
                $(e2.getEntity().getProperty()).isNotNull());
        query.list($(e2));
        Join join = (Join) query.getJoinBuilder().getTupleExpr();

        // entity
        StatementPattern entity = (StatementPattern) ((Join) join.getArg(0)).getArg(1);
        assertEquals(TEST.NS + "entity", entity.getPredicateVar().getValue().stringValue());
        assertNull(entity.getContextVar());

        // entity.property
        StatementPattern property = (StatementPattern) join.getArg(1);
        assertEquals(TEST.NS + "property", property.getPredicateVar().getValue().stringValue());
        assertEquals(NS3, property.getContextVar().getValue().stringValue());
    }

    @Test
    public void Entity3_property(){
        SesameQuery query = (SesameQuery) session.from($(e3)).where($(e3.getProperty()).isNotNull());
        query.list($(e3));
        Join join = (Join) query.getJoinBuilder().getTupleExpr();

        // rdf:type
        StatementPattern rdf_type = (StatementPattern) join.getArg(0);
        assertEquals(RDF.type.getId(), rdf_type.getPredicateVar().getValue().stringValue());
        assertNull(rdf_type.getContextVar());

        // property
        StatementPattern property = (StatementPattern) join.getArg(1);
        assertEquals(TEST.NS + "property", property.getPredicateVar().getValue().stringValue());
        assertEquals(NS3, property.getContextVar().getValue().stringValue());
    }

    @Test
    public void Entity3_entity(){
        SesameQuery query = (SesameQuery) session.from($(e3)).where(
                $(e3.getEntity()).isNotNull(),
                $(e3.getEntity().getProperty()).isNotNull());
        query.list($(e3));
        Join join = (Join) query.getJoinBuilder().getTupleExpr();

        // entity
        StatementPattern entity = (StatementPattern) ((Join) join.getArg(0)).getArg(1);
        assertEquals(TEST.NS + "entity", entity.getPredicateVar().getValue().stringValue());
        assertNull(entity.getContextVar());

        // entity.property
        StatementPattern property = (StatementPattern) join.getArg(1);
        assertEquals(TEST.NS + "property", property.getPredicateVar().getValue().stringValue());
        assertEquals(NS1, property.getContextVar().getValue().stringValue());
    }

}
