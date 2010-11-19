/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.guice;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mysema.rdfbean.model.Repository;
import com.mysema.rdfbean.object.Configuration;
import com.mysema.rdfbean.sesame.MemoryRepository;

/**
 * RDFBeanModuleTest provides
 * 
 * @author tiwe
 * @version $Id$
 */
public class RDFBeanSubModuleTest {

    private static Injector injector;

    @BeforeClass
    public static void setUp() {
        injector = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {

                bind(ServiceA.class);
                bind(ServiceB.class).to(ServiceBImpl.class);
                bind(ServiceC.class);
                bind(ServiceD.class).to(ServiceDImpl.class);

                RDFBeanModule rdfBeanModule = new RDFBeanModule() {
                    @Override
                    public Repository createRepository(Configuration configuration) {
                        return new MemoryRepository();
                    }
                };

                install(rdfBeanModule);
            }
        });
    }

    @Test
    public void tx1() {
        ServiceA service = injector.getInstance(ServiceA.class);
        service.nonTxMethod();
        service.nonTxMethod2();
        service.txMethod();
        service.txMethod2();
        service.txMethod3();
        service.txReadonly();
    }

    @Test
    public void tx2() {
        ServiceB service = injector.getInstance(ServiceB.class);
        service.txMethod();
        service.txReadonly();
        service.nonTxMethod();
    }

    @Test
    public void tx3() {
        ServiceC service = injector.getInstance(ServiceC.class);
        service.txMethod();
        service.nonTxMethod();
    }

    @Test
    public void tx4() {
        ServiceD service = injector.getInstance(ServiceD.class);
        service.txMethod();
    }

    @Test(expected = Exception.class)
    public void txMethodWithException_commit() throws Exception {
        ServiceA service = injector.getInstance(ServiceA.class);
        service.txMethodWithException_commit();
    }

    @Test(expected = Exception.class)
    public void txMethodWithException_rollback() throws Exception {
        ServiceA service = injector.getInstance(ServiceA.class);
        service.txMethodWithException_rollback();
    }
}