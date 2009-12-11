/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.lucene;

import java.io.File;
import java.util.UUID;

import org.compass.core.config.CompassConfiguration;
import org.junit.After;
import org.junit.Before;

import com.mysema.rdfbean.TEST;
import com.mysema.rdfbean.object.Configuration;

/**
 * AbstractRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public abstract class AbstractLuceneTest {

    protected LuceneRepository luceneRepository;
    
    @Before
    public void setUp(){
        new File("target/test-index").renameTo(new File("target/" + UUID.randomUUID()));
        
        CompassConfiguration compassConfig = new CompassConfiguration();
        compassConfig.configure("/compass.xml");
        
        LuceneConfiguration configuration = new LuceneConfiguration();
        configuration.setCoreConfiguration(getCoreConfiguration());
        configuration.setDefaultPropertyConfig(getDefaultPropertyConfig());
        configuration.setCompassConfig(compassConfig);
        configuration.addPrefix("test", TEST.NS);
        luceneRepository = new LuceneRepository(configuration);
    }
    
    protected abstract Configuration getCoreConfiguration();
    
    protected PropertyConfig getDefaultPropertyConfig(){
        return null;
    }

    @After
    public void tearDown(){
        luceneRepository.close();
    }
}
