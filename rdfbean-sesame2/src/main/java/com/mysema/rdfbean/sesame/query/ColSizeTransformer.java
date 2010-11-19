/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.sesame.query;

import java.util.Collection;

import org.openrdf.query.algebra.Exists;
import org.openrdf.query.algebra.Not;
import org.openrdf.query.algebra.ValueExpr;
import org.openrdf.query.algebra.Var;

import com.mysema.query.types.Constant;
import com.mysema.query.types.Operation;
import com.mysema.query.types.Operator;
import com.mysema.query.types.Ops;
import com.mysema.query.types.Path;
import com.mysema.rdfbean.model.RDF;

/**
 * ColSizeTransformer provides
 *
 * @author tiwe
 * @version $Id$
 */
public class ColSizeTransformer implements OperationTransformer{

    @Override
    public Collection<? extends Operator<?>> getSupportedOperations() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ValueExpr transform(Operation<?> operation, TransformerContext context) {
        Operator<?> op = operation.getOperator();
        int size = ((Constant<Integer>) operation.getArg(1)).getConstant().intValue();
        if (op == Ops.GOE){
            op = Ops.GT;
            size--;
        }else if (op == Ops.LOE){
            op = Ops.LT;
            size++;
        }
        
        JoinBuilder builder = context.createJoinBuilder();
        // path from size operation
        Path<?> path = (Path<?>)((Operation<?>)operation.getArg(0)).getArg(0); 
        Var pathVar = context.toVar(path);                                
        for (int i=0; i < size-1; i++){
            Var rest = context.createVar();
            context.match(builder, pathVar, RDF.rest, rest, null); // TODO : context
            pathVar = rest;
        }
        
        // last
        if (op == Ops.EQ_PRIMITIVE){
            context.match(builder, pathVar, RDF.rest, context.toVar(RDF.nil), null); // TODO : context
            
        }else if (op == Ops.GT){
            Var next = context.createVar();
            context.match(builder, pathVar, RDF.rest, next, null); // TODO : context
            context.match(builder, next, RDF.rest, context.createVar(), null); // TODO : context
            
        }else if (op == Ops.LT){
            context.match(builder, pathVar, RDF.rest, context.createVar(), null); // TODO : context
        }          
        
        if (op != Ops.LT){
            return new Exists(builder.getTupleExpr());    
        }else{
            return new Not(new Exists(builder.getTupleExpr()));
        }    
    }

}