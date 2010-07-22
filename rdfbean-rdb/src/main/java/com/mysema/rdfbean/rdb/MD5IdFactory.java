/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.rdfbean.rdb;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.mysema.rdfbean.model.LIT;
import com.mysema.rdfbean.model.NODE;
import com.mysema.rdfbean.model.RDF;
import com.mysema.rdfbean.model.RDFS;
import com.mysema.rdfbean.model.RepositoryException;
import com.mysema.rdfbean.model.UID;
import com.mysema.rdfbean.model.XSD;
import com.mysema.rdfbean.owl.OWL;

/**
 * MD5IdFactory is a MD5 hash based implementation of the IdFactory interface
 *
 * @author tiwe
 * @version $Id$
 */
public class MD5IdFactory implements IdFactory {
    
    private final int[] masks = new int[]{-128,191,127,63};
    
    private final Map<UID,String> uid2string = new HashMap<UID,String>();
    
    public MD5IdFactory() {
        register("rdf",RDF.ALL);
        register("rdfs",RDFS.ALL);
        register("owl",OWL.ALL);
        register("xsd",XSD.ALL);
    }
    
    private void register(String prefix, Collection<UID> all) {
        for (UID id : all){
            uid2string.put(id, prefix+":"+id.ln());
        }
    }

    private String getReadableURI(UID uid) {
        String rv = uid2string.get(uid);
        if (rv == null){
            rv = uid.getId();
        }
        return rv;
    }
    
    @Override
    public Long getId(NODE node) {
        int mask;
        String value;
        if (node.isLiteral()){
            LIT literal = node.asLiteral();            
            if (literal.getLang() != null){
                mask = masks[0];
                value = literal.getValue() + literal.getLang();
            }else{
                mask = masks[1];
                value = literal.getValue() + getReadableURI(literal.getDatatype());
            }
        }else if (node.isBNode()){
            mask = masks[2];
            value = node.getValue();
        }else{
            mask = masks[3];
            value = getReadableURI(node.asURI());
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(value.getBytes("UTF-8"));
            byte[] hash = digest.digest();
            byte[] longBytes = new byte[8];
            System.arraycopy(hash, 0, longBytes, 0, longBytes.length);
            longBytes[0] = (byte)(longBytes[0] & mask);            
            return new BigInteger(longBytes).longValue();
        } catch (NoSuchAlgorithmException e) {
            throw new RepositoryException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RepositoryException(e);
        }
        
    }

    @Override
    public Integer getId(Locale locale) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(locale.toString().getBytes("UTF-8"));
            byte[] hash = digest.digest();
            byte[] intBytes = new byte[4];
            System.arraycopy(hash, 0, intBytes, 0, intBytes.length);
            return new BigInteger(intBytes).intValue();
        } catch (NoSuchAlgorithmException e) {
            throw new RepositoryException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RepositoryException(e);
        }
    }

}
