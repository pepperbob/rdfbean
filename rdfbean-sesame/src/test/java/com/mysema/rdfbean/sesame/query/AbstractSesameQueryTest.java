/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.sesame.query;


import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.repository.RepositoryException;

import com.mysema.query.grammar.types.Expr;
import com.mysema.query.grammar.types.PathMetadata;
import com.mysema.query.grammar.types.Path.PEntity;
import com.mysema.rdfbean.TEST;
import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Id;
import com.mysema.rdfbean.annotations.Localized;
import com.mysema.rdfbean.annotations.Path;
import com.mysema.rdfbean.annotations.Predicate;
import com.mysema.rdfbean.object.BeanQuery;
import com.mysema.rdfbean.object.BeanQueryAdapter;
import com.mysema.rdfbean.sesame.SesameSession;
import com.mysema.rdfbean.sesame.SessionTestBase;
import com.mysema.rdfbean.sesame.query.serializer.QuerySerializer;


/**
 * AbstractSailQueryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public abstract class AbstractSesameQueryTest extends SessionTestBase {
    
    @ClassMapping(ns =TEST.NS, ln="TestType1")
    public static class TestType{        
        @Id 
        String id;        
        @Predicate(ln="directProperty1")  
        String directProperty;                
        @Predicate
        @Localized
        String localizedProperty;        
        @Predicate(ln="localizedProperty")
        @Localized
        Map<Locale,String> localizedAsMap;        
        @Predicate 
        String notExistantProperty;              
        @Predicate
        int numericProperty;        
        @Predicate(ln="listProperty") 
        List<TestType2> listProperty;        
        @Predicate(ln="setProperty") 
        Set<TestType2> setProperty;        
        
        public String getId() {
            return id;
        }

        public String getDirectProperty() {
            return directProperty;
        }

        public String getLocalizedProperty() {
            return localizedProperty;
        }

        public Map<Locale, String> getLocalizedAsMap() {
            return localizedAsMap;
        }

        public String getNotExistantProperty() {
            return notExistantProperty;
        }

        public int getNumericProperty() {
            return numericProperty;
        }

        public List<TestType2> getListProperty() {
            return listProperty;
        }

        public Set<TestType2> getSetProperty() {
            return setProperty;
        }

        public String toString(){
            return directProperty;
        }
    }
    
    @ClassMapping(ns = TEST.NS, ln="TestType2")
    public static class TestType2{
        @Path({@Predicate(ln="testType"), @Predicate(ln="directProperty")}) 
        String pathProperty;                            
        @Predicate(ln="directProperty2") 
        String directProperty;        
        
        public String getPathProperty() {
            return pathProperty;
        }

        public String getDirectProperty() {
            return directProperty;
        }

        public String toString(){
            return directProperty;
        }
    }
    
    public static class QTestType extends PEntity<TestType>{
        public QTestType(String var) {
            super(TestType.class, "TestType", var);
        }        
        public final PString id = _string("id");
        public final PString directProperty = _string("directProperty");
        public final PString localizedProperty = _string("localizedProperty");
        public final PComponentMap<java.util.Locale,java.lang.String> localizedAsMap = _simplemap("localizedAsMap",java.util.Locale.class,java.lang.String.class);       
        public final PNumber<Integer> numericProperty = _number("numericProperty",Integer.class);
        public final PString notExistantProperty = _string("notExistantProperty");
        public final com.mysema.query.grammar.types.Path.PEntityList<TestType2> listProperty = _entitylist("listProperty",TestType2.class,"TestType2");
        public final com.mysema.query.grammar.types.Path.PEntityCollection<TestType2> setProperty = _entitycol("setProperty",TestType2.class,"TestType2");
        public QTestType2 listProperty(int index) {
            return new QTestType2(PathMetadata.forListAccess(listProperty,index));
        }
        public QTestType2 listProperty(Expr<Integer> index) {
            return new QTestType2(PathMetadata.forListAccess(listProperty,index));
        }
    }
    
    public static class QTestType2 extends PEntity<TestType2>{
        public QTestType2(String var) {
            super(TestType2.class, "TestType2", var);
        }
        public QTestType2(PathMetadata<?> pm) {
            super(TestType2.class, "TestType2", pm);
        }
        public final PString pathProperty = _string("pathProperty");
        public final PString directProperty = _string("directProperty");    
        
    }
    
    protected QTestType var = new QTestType("var");
    
    protected QTestType2 var2 = new QTestType2("var2");
    
    protected SesameSession session;
    
    protected List<TestType> instances;
    
    protected TestType instance;
    
    @Before
    public void setUp() throws RepositoryException{
        session = createSession(FI, TestType.class, TestType2.class);
    }
    
    @After
    public void tearDown(){
        System.out.println();
    }
    
    protected BeanQuery newQuery(){
        SesameQuery query = new SesameQuery(session, 
                StatementPattern.Scope.NAMED_CONTEXTS){
            @Override
            protected void logQuery(ParsedTupleQuery query) {
                System.out.println(new QuerySerializer(query,true).toString());
            }
        };
        return new BeanQueryAdapter(query, query);
    }
    
    protected BeanQuery where(Expr.EBoolean... e){
        return newQuery().from(var).where(e);
    }
}