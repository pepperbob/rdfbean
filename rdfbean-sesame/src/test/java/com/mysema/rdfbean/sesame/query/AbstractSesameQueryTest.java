/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.sesame.query;


import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.parser.TupleQueryModel;
import org.openrdf.store.StoreException;

import com.mysema.query.types.expr.EBoolean;
import com.mysema.rdfbean.object.BeanQuery;
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionImpl;
import com.mysema.rdfbean.sesame.SesameConnection;
import com.mysema.rdfbean.sesame.SesameDialect;
import com.mysema.rdfbean.sesame.SessionTestBase;


/**
 * AbstractSailQueryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public abstract class AbstractSesameQueryTest extends SessionTestBase {
    
    protected QSimpleType var = new QSimpleType("var");
    
    protected QSimpleType2 var2 = new QSimpleType2("var2");
    
    protected Session session;
    
    protected SesameConnection connection;
    
    protected List<SimpleType> instances;
    
    @Before
    public void setUp() throws StoreException{
        session = createSession(FI, SimpleType.class, SimpleType2.class);
        connection = (SesameConnection) ((SessionImpl) session).getConnection();
    }
    
    @After
    public void tearDown(){
        System.out.println();
    }
    
    protected BeanQuery newQuery(){
        SesameQuery query = new SesameQuery(session, 
                (SesameDialect) connection.getDialect(),
                connection.getConnection(),
                StatementPattern.Scope.NAMED_CONTEXTS,
                true){
            @Override
            protected void logQuery(TupleQueryModel query) {
                System.out.println(new QuerySerializer(query,true).toString());
            }
        };
        return query;
    }
    
    protected BeanQuery where(EBoolean... e){
        return newQuery().from(var).where(e);
    }
}
