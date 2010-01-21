/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.query;

import java.util.List;

import javax.annotation.Nullable;

import net.jcip.annotations.Immutable;

import com.mysema.query.QueryMetadata;
import com.mysema.query.paging.ListSource;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.EBoolean;
import com.mysema.query.types.path.PEntity;
import com.mysema.query.types.path.Path;
import com.mysema.rdfbean.object.BeanQuery;
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionCallback;
import com.mysema.rdfbean.object.SessionFactory;

/**
 * ListSourceBase provides
 *
 * @author tiwe
 * @version $Id$
 */
@Immutable
public class BeanListSource<T> implements ListSource<T>{
    
    @Nullable
    private final EBoolean condition;
    
    private final OrderSpecifier<?>[] order;
    
    private final Path<T> projection;
    
    private final SessionFactory sessionFactory;
    
    private final PEntity<?>[] sources;
    
    private final long size;
    
    public BeanListSource(SessionFactory sessionFactory,  
            PEntity<?>[] sourceArray, 
            QueryMetadata metadata,
            Path<T> projection){
        this.sessionFactory = sessionFactory;
        this.sources = sourceArray;
        this.condition = metadata.getWhere();
        this.projection = projection;
        this.order = metadata.getOrderBy().toArray(new OrderSpecifier[0]);    
        this.size = sessionFactory.execute(new SessionCallback<Long>(){
            @Override
            public Long doInSession(Session session) {
                BeanQuery countQry = session.from(sources);
                if (condition != null){
                    countQry.where(condition);
                }
                return countQry.count();
            }            
        });   
    }
    
    public List<T> getResults(final int from, final int to){
        return sessionFactory.execute(new SessionCallback<List<T>>(){
            public List<T> doInSession(Session session){
                BeanQuery qry = session.from(sources).offset(from).limit(to - from).orderBy(order);
                if (condition != null){
                    qry.where(condition);
                }
                return qry.list(projection.asExpr());
            }
        });
    }
    
    public final boolean isEmpty(){
        return size == 0l;
    }

    public final long size(){
        return size;   
    }

}