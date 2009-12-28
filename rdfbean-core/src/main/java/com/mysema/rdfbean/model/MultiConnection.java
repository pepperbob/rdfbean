package com.mysema.rdfbean.model;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.commons.lang.Assert;
import com.mysema.commons.lang.CloseableIterator;
import com.mysema.rdfbean.object.BeanQuery;
import com.mysema.rdfbean.object.RDFBeanTransaction;
import com.mysema.rdfbean.object.Session;

/**
 * MultiConnection provides
 *
 * @author tiwe
 * @version $Id$
 */
public abstract class MultiConnection implements RDFConnection{
    
    private static final Logger logger = LoggerFactory.getLogger(MultiConnection.class);

    private final RDFConnection[] connections;
    
    private RDFBeanTransaction localTxn;
    
    private boolean readonlyTnx;
    
    public MultiConnection(RDFConnection... connections){
        this.connections = Assert.notNull(connections);
    }
    
    @Override
    public RDFBeanTransaction beginTransaction(boolean readOnly, int txTimeout,
            int isolationLevel) {
        RDFBeanTransaction[] transactions = new RDFBeanTransaction[connections.length];
        for (int i = 0; i < transactions.length; i++){
            transactions[i] = connections[i].beginTransaction(readOnly, txTimeout, isolationLevel);
        }
        this.readonlyTnx = readOnly;
        localTxn = new MultiTransaction(this, transactions);
        return localTxn;
    }

    @Override
    public void clear() {
        for (RDFConnection connection : connections){
            connection.clear();
        }        
    }
    
    public void cleanUpAfterCommit(){
        localTxn = null;
        readonlyTnx = false;
    }
    
    public void cleanUpAfterRollback(){
        try {
            localTxn = null;
            readonlyTnx = false;
            close();
        } catch (IOException e) {
            String error = "Caught " + e.getClass().getName();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    @Override
    public BeanQuery createQuery(Session session) {
        return connections[0].createQuery(session);
    }

    @Override
    public CloseableIterator<STMT> findStatements(ID subject, UID predicate,
            NODE object, UID context, boolean includeInferred) {
        return connections[0].findStatements(subject, predicate, object, context, includeInferred);
    }

    @Override
    public void update(Set<STMT> removedStatements, Set<STMT> addedStatements) {
        if (!readonlyTnx){
            for (RDFConnection connection : connections){
                connection.update(removedStatements, addedStatements);
            }    
        }                
    }

    @Override
    public void close() throws IOException {
        for (RDFConnection connection : connections){
            connection.close();
        }        
    }

    @Override
    public BID createBNode() {
        return connections[0].createBNode();
    }

}