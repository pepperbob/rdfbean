/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.rdfbean.model.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.mysema.rdfbean.model.BID;
import com.mysema.rdfbean.model.NODE;
import com.mysema.rdfbean.model.RepositoryException;
import com.mysema.rdfbean.model.STMT;

/**
 * @author tiwe
 *
 */
public class NTriplesWriter implements RDFWriter{

    private final Writer writer;
    
    private final Map<BID,String> nodeIds = new HashMap<BID,String>();
    
    public NTriplesWriter(OutputStream out) {
        try {
            writer = new OutputStreamWriter(out, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RepositoryException(e);
        }
    }
    
    public NTriplesWriter(Writer writer) {
        this.writer = writer;
    }
    
    
    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }

    @Override
    public void end() {
        // do nothing        
    }

    @Override
    public void handle(STMT stmt) {
        try {
            writer.write(toString(stmt) + " .\n");
        } catch (IOException e) {
            throw new RepositoryException(e);
        }        
    }

    @Override
    public void namespace(String prefix, String namespace) {
        // do nothing
    }

    @Override
    public void start() {
        // do nothing        
    }
    

    protected String toString(NODE node){
        if (node.isURI()){
            return "<" + node.getValue() + ">";
        }else if (node.isBNode()){    
            String nodeID = nodeIds.get(node);
            if (nodeID == null){
                nodeID = "node" + (nodeIds.size()+1);
                nodeIds.put(node.asBNode(), nodeID);
            }
            return "_:" + nodeID;
        }else{
            return node.toString();
        }
    }

    protected String toString(STMT stmt){
        return toString(stmt.getSubject()) + " " 
            + toString(stmt.getPredicate()) + " " 
            + toString(stmt.getObject());
    }
    

}
