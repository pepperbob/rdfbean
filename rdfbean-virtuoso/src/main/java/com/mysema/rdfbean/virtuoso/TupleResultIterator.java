package com.mysema.rdfbean.virtuoso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.query.QueryException;
import com.mysema.rdfbean.model.NODE;

/**
 * @author tiwe
 *
 */
public class TupleResultIterator implements CloseableIterator<Map<String, NODE>>{

    private static final Logger logger = LoggerFactory.getLogger(TupleResultIterator.class);
    
    private final Statement stmt;
    
    private final ResultSet rs;
    
    private final String query;
    
    private final Converter converter;
    
    private final Map<String, NODE> bindings;
    
    private final List<String> variables;
    
    @Nullable
    private Boolean next;
    
    public TupleResultIterator(Statement stmt, 
            ResultSet rs, 
            String query, 
            Converter converter,
            List<String> variables, 
            Map<String, NODE> bindings) {
        this.stmt = stmt;
        this.rs = rs;
        this.query = query;
        this.converter = converter;
        this.variables = variables;
        this.bindings = bindings;
    }

    @Override
    public void close() {
        AbstractQueryImpl.close(stmt, rs);
    }

    @Override
    public boolean hasNext() {
        if (next == null){
            try {
                next = rs.next();
            } catch (SQLException e) {
                close();
                logger.warn("Caught exception for query " + query, e);
                throw new QueryException(e);
            }
        }
        return next;
    }

    @Override
    public Map<String, NODE> next() {
        if (hasNext()){
            try {
                next = null;
                Map<String,NODE> tuples = new HashMap<String,NODE>();
                for (String variable : variables){
                    Object obj = rs.getObject(variable);
                    if (obj != null) {
                        tuples.put(variable, converter.toNODE(obj));    
                    } else if (bindings.containsKey(variable)) {
                        tuples.put(variable, bindings.get(variable));
                    }                    
                }
//                for (Map.Entry<String, NODE> entry : bindings.entrySet()){
//                    if (!tuples.containsKey(entry.getKey())){
//                        tuples.
//                    }
//                }
                return tuples;
            } catch (SQLException e) {
                close();
                logger.warn("Caught exception for query " + query, e);                
                throw new QueryException(e);
            }
        }else{
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();        
    }

}
