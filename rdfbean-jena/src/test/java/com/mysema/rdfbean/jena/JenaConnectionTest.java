package com.mysema.rdfbean.jena;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.Test;

import com.mysema.commons.lang.IteratorAdapter;
import com.mysema.rdfbean.TEST;
import com.mysema.rdfbean.model.*;

public class JenaConnectionTest extends AbstractConnectionTest {

    private static final UID context = new UID(TEST.NS);

    @Test
    public void Update_with_nulls() {
        connection.update(Collections.<STMT> emptySet(), null);
        connection.update(null, Collections.<STMT> emptySet());
        connection.update(null, null);
    }

    @Test
    public void Exists() {
        UID context = new UID(TEST.NS);
        assertFalse(connection.exists(null, null, null, context, false));
        connection.update(null, Collections.singleton(new STMT(new BID(), RDF.type, RDFS.Class, context)));
        assertTrue(connection.exists(null, null, null, context, false));
    }

    @Test
    public void Exists2() {
        ID sub = new UID(TEST.NS, "e" + System.currentTimeMillis());
        ID type = new UID(TEST.NS, "TestType" + System.currentTimeMillis());
        assertNotExists(sub, null, null, null);
        repository.execute(new Addition(new STMT(sub, RDF.type, type)));

        assertExists(new STMT(sub, RDF.type, type));
    }

    @Test
    public void FindStatements() {
        ID sub = new UID(TEST.NS, UUID.randomUUID().toString());
        assertTrue(IteratorAdapter.asList(connection.findStatements(sub, null, null, null, false)).isEmpty());
        repository.execute(new Addition(new STMT(sub, RDF.type, RDFS.Class)));

        assertFalse(findStatements(sub, null, null, null).isEmpty());
        assertFalse(findStatements(sub, RDF.type, null, null).isEmpty());
        assertFalse(findStatements(null, RDF.type, RDFS.Class, null).isEmpty());
    }

    @Test
    public void FindStatements_from_Context() {
        UID sub = new UID(TEST.NS, UUID.randomUUID().toString());
        assertTrue(findStatements(sub, null, null, null).isEmpty());
        repository.execute(new Addition(new STMT(sub, RDF.type, RDFS.Class, context)));

        assertFalse(findStatements(sub, null, null, null).isEmpty());
        assertFalse(findStatements(sub, null, null, context).isEmpty());
        assertFalse(findStatements(null, null, null, context).isEmpty());
    }

    @Test
    public void Resources() {
        ID sub = new UID(TEST.NS, "e" + System.currentTimeMillis());
        List<STMT> stmts = Arrays.asList(new STMT(sub, RDFS.label, sub));

        connection.update(null, stmts);

        List<STMT> found = findStatements(sub, null, null, null);
        assertEquals(new HashSet<STMT>(stmts), new HashSet<STMT>(found));
        assertExists(stmts.get(0));
    }

    @Test
    public void Literals() {
        ID sub = new UID(TEST.NS, "e" + System.currentTimeMillis());
        List<STMT> stmts = Arrays.asList(
                new STMT(sub, RDFS.label, new LIT(sub.getId())),
                new STMT(sub, RDFS.label, new LIT("X")),
                new STMT(sub, RDFS.label, new LIT(sub.getId(), Locale.ENGLISH)),
                new STMT(sub, RDFS.label, new LIT("1", XSD.intType))
                );
        connection.update(null, stmts);

        List<STMT> found = findStatements(sub, null, null, null);
        assertEquals(new HashSet<STMT>(stmts), new HashSet<STMT>(found));

        // find int literal
        assertExists(new STMT(sub, RDFS.label, new LIT("1", XSD.intType)));
        // find string literal
        assertExists(new STMT(sub, RDFS.label, new LIT("X")));
        // find string literal
        assertExists(new STMT(sub, RDFS.label, new LIT(sub.getId())));
    }

    @Test
    public void BlankNodes() {
        ID sub = new BID();
        ID obj = new BID();
        List<STMT> stmts = Collections.singletonList(new STMT(sub, RDF.type, obj));
        connection.update(null, stmts);

        List<STMT> found = findStatements(sub, null, null, null);
        assertEquals(new HashSet<STMT>(stmts), new HashSet<STMT>(found));

        assertExists(stmts.get(0));
    }

    @Test
    public void Remove_subject_and_object_given() {
        // FIXME: this is too slow
        ID sub = new UID(TEST.NS, "s" + System.currentTimeMillis());
        ID obj = new UID(TEST.NS, "o" + System.currentTimeMillis());
        STMT stmt = new STMT(sub, RDF.type, obj);
        List<STMT> stmts = Collections.singletonList(stmt);
        connection.update(null, stmts);

        assertExists(stmt.getSubject(), null, null, null);
        connection.remove(sub, null, obj, null);
        assertNotExists(stmt.getSubject(), null, null, null);
    }

    @Test
    public void Remove_subject_and_literal_object_given() {
        ID sub = new UID(TEST.NS, "s" + System.currentTimeMillis());
        NODE obj = new LIT(TEST.NS + "o" + System.currentTimeMillis());
        STMT stmt = new STMT(sub, RDF.type, obj);
        List<STMT> stmts = Collections.singletonList(stmt);

        connection.update(null, stmts);

        assertExists(stmt.getSubject(), null, null, null);
        connection.remove(sub, null, obj, null);
        assertNotExists(stmt.getSubject(), null, null, null);
    }

    @Test
    public void Remove_subject_and_predicate_given() {
        ID sub = new UID(TEST.NS, "s" + System.currentTimeMillis());
        ID obj = new UID(TEST.NS, "o" + System.currentTimeMillis());
        List<STMT> stmts = Arrays.asList(
                new STMT(sub, RDF.type, obj),
                new STMT(sub, RDFS.label, new LIT("X"))
                );

        connection.update(null, stmts);

        assertExists(sub, null, null, null);
        connection.remove(sub, RDF.type, null, null);
        assertNotExists(sub, RDF.type, null, null);
        assertExists(sub, RDFS.label, null, null);
    }

    @Test
    public void Remove_none_given_from_default() {
        ID sub = new UID(TEST.NS, "s" + System.currentTimeMillis());
        ID obj = new UID(TEST.NS, "o" + System.currentTimeMillis());
        STMT stmt = new STMT(sub, RDF.type, obj);
        List<STMT> stmts = Collections.singletonList(stmt);

        connection.update(null, stmts);

        assertExists(stmt.getSubject(), null, null, null);
        connection.remove(null, null, null, null);
        assertNotExists(stmt.getSubject(), null, null, null);
    }

    @Test
    public void Remove_none_given_from_named() {
        ID sub = new UID(TEST.NS, "s" + System.currentTimeMillis());
        ID obj = new UID(TEST.NS, "o" + System.currentTimeMillis());
        STMT stmt = new STMT(sub, RDF.type, obj, context);
        List<STMT> stmts = Collections.singletonList(stmt);

        connection.update(null, stmts);

        assertExists(stmt.getSubject(), null, null, null);
        connection.remove(null, null, null, null);
        assertNotExists(stmt.getSubject(), null, null, null);
    }

    @Test
    public void Remove_all_given_from_default() {
        ID sub = new UID(TEST.NS, "s" + System.currentTimeMillis());
        ID obj = new UID(TEST.NS, "o" + System.currentTimeMillis());
        STMT stmt = new STMT(sub, RDF.type, obj);
        List<STMT> stmts = Collections.singletonList(stmt);
        connection.update(null, stmts);

        assertExists(stmt.getSubject(), null, null, null);
        connection.remove(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), stmt.getContext());
        assertNotExists(stmt.getSubject(), null, null, null);
    }

    @Test
    public void Remove_all_given_from_named() {
        ID sub = new UID(TEST.NS, "s" + System.currentTimeMillis());
        ID obj = new UID(TEST.NS, "o" + System.currentTimeMillis());
        STMT stmt = new STMT(sub, RDF.type, obj, context);
        List<STMT> stmts = Collections.singletonList(stmt);

        connection.update(null, stmts);

        assertExists(stmt.getSubject(), null, null, null);
        connection.remove(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), stmt.getContext());
        assertNotExists(stmt.getSubject(), null, null, null);
    }

    @Test
    public void Remove_subject_given() {
        ID sub = new UID(TEST.NS, "s" + System.currentTimeMillis());
        ID obj = new UID(TEST.NS, "o" + System.currentTimeMillis());
        STMT stmt = new STMT(sub, RDF.type, obj);
        List<STMT> stmts = Collections.singletonList(stmt);
        connection.update(null, stmts);

        assertExists(stmt.getSubject(), null, null, null);
        connection.remove(stmt.getSubject(), null, null, null);
        assertNotExists(stmt.getSubject(), null, null, null);
    }

    @Test
    public void Remove_Literals_from_named() {
        ID sub = new UID(TEST.NS, "el" + System.currentTimeMillis());
        List<STMT> stmts = Arrays.asList(
                // new STMT(sub, RDFS.label, new LIT(sub.getId(), new
                // UID(TEST.NS)), context),
                new STMT(sub, RDFS.label, new LIT(sub.getId()), context),
                new STMT(sub, RDFS.label, new LIT(sub.getId(), Locale.ENGLISH), context),
                new STMT(sub, RDFS.label, new LIT("1", XSD.intType), context),
                new STMT(sub, RDFS.label, new LIT("20011010", XSD.date), context)
                );
        connection.update(null, stmts);

        for (STMT stmt : stmts) {
            connection.remove(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), stmt.getContext());
            assertFalse(stmt.toString(), connection.exists(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), stmt.getContext(), false));
        }
        assertNotExists(sub, null, null, null);
    }

    @Test
    public void Remove_Literals_from_default() {
        ID sub = new UID(TEST.NS, "el" + System.currentTimeMillis());
        List<STMT> stmts = Arrays.asList(
                // new STMT(sub, new UID(TEST.NS), new LIT("x", new
                // UID(TEST.NS))),
                // new STMT(sub, new UID(TEST.NS), new LIT("1", new
                // UID(TEST.NS))),
                // new STMT(sub, RDFS.label, new LIT(sub.getId(), new
                // UID(TEST.NS))),
                new STMT(sub, RDFS.label, new LIT(sub.getId())),
                new STMT(sub, RDFS.label, new LIT(sub.getId(), Locale.ENGLISH)),
                new STMT(sub, RDFS.label, new LIT("1", XSD.intType)),
                new STMT(sub, new UID(TEST.NS), new LIT("1", XSD.intType)),
                new STMT(sub, RDFS.label, new LIT("20011010", XSD.date)),
                new STMT(sub, new UID(TEST.NS), new LIT("20011010", XSD.date))
                );

        connection.update(null, stmts);

        for (STMT stmt : stmts) {
            connection.remove(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), stmt.getContext());
            assertFalse(stmt.toString(), connection.exists(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), stmt.getContext(), false));
        }
        for (STMT stmt : findStatements(sub, null, null, null)) {
            System.err.println(stmt);
        }
        assertNotExists(sub, null, null, null);
    }

    @Test
    public void Update() {
        ID sub = new UID(TEST.NS, "s" + System.currentTimeMillis());
        ID type = new UID(TEST.NS, "TestType" + System.currentTimeMillis());
        Collection<STMT> stmts = Collections.singleton(new STMT(sub, RDF.type, type));
        // add
        connection.update(null, stmts);
        assertExists(sub, RDF.type, type, null);
        // remove
        connection.update(stmts, null);
        assertNotExists(sub, RDF.type, type, null);
    }

    @Test
    public void Update_named() {
        ID sub = new UID(TEST.NS, "s" + System.currentTimeMillis());
        ID type = new UID(TEST.NS, "TestType" + System.currentTimeMillis());
        Collection<STMT> stmts = Collections.singleton(new STMT(sub, RDF.type, type, context));
        // add
        connection.update(null, stmts);
        assertExists(sub, RDF.type, type, null);
        // remove
        connection.update(stmts, null);
        assertNotExists(sub, RDF.type, type, null);
    }
}
