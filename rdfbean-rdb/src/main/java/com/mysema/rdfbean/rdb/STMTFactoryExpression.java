package com.mysema.rdfbean.rdb;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.mysema.query.types.Constant;
import com.mysema.query.types.Expression;
import com.mysema.query.types.FactoryExpression;
import com.mysema.query.types.Visitor;
import com.mysema.rdfbean.model.BID;
import com.mysema.rdfbean.model.ID;
import com.mysema.rdfbean.model.NODE;
import com.mysema.rdfbean.model.PatternBlock;
import com.mysema.rdfbean.model.STMT;
import com.mysema.rdfbean.model.UID;

public class STMTFactoryExpression implements FactoryExpression<STMT> {

    private static final long serialVersionUID = 2264837934860429836L;

    private final List<Expression<?>> args;

    private final Function<Long, NODE> function;

    @Nullable
    private final ID subject;

    @Nullable
    private final UID predicate;

    @Nullable
    private final NODE object;

    @Nullable
    private final UID context;

    public STMTFactoryExpression(PatternBlock pattern, List<Expression<?>> args, Function<Long, NODE> function) {
        this.args = args;
        this.function = function;
        this.subject = (ID) getConstant(pattern.getSubject());
        this.predicate = (UID) getConstant(pattern.getPredicate());
        this.object = getConstant(pattern.getObject());
        this.context = (UID) getConstant(pattern.getContext());
    }

    @Nullable
    private NODE getConstant(Expression<?> expr) {
        if (expr instanceof Constant<?>) {
            return (NODE) ((Constant<?>) expr).getConstant();
        } else {
            return null;
        }
    }

    @Override
    public List<Expression<?>> getArgs() {
        return args;
    }

    @Override
    public STMT newInstance(Object... args) {
        int counter = 0;
        ID s = subject != null ? subject : getId(args[counter++]);
        UID p = predicate != null ? predicate : (UID) getId(args[counter++]);
        NODE o = object != null ? object : function.apply((Long) args[counter++]);
        UID c = context;
        if (args.length > counter && c != null) {
            c = (UID) getId(args[counter++]);
        }
        return new STMT(s, p, o, c);
    }

    private ID getId(Object input) {
        if (input instanceof Long) {
            return (ID) function.apply((Long) input);
        } else {
            String val = input.toString();
            return val.contains(":") ? new UID(val) : new BID(val);
        }
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public Class<? extends STMT> getType() {
        return STMT.class;
    }

}
