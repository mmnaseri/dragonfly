package com.agileapes.dragonfly.query.impl.functions;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.TypedMethodModel;

import java.util.Collection;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 14:38)
 */
public class QuoteMethod extends TypedMethodModel {

    private String escape;

    public QuoteMethod(Character escape) {
        this.escape = String.valueOf(escape);
        if (this.escape.equals("\\")) {
            this.escape = "\\\\";
        }
    }

    @Invokable
    public Object quote(Object item) {
        if (item != null && item instanceof String) {
            return "'" + item.toString().replaceAll("([^" + escape + "]|^)'", "$1" + escape + "'") + "'";
        }
        return item;
    }

    @Invokable
    public Collection<?> quote(Collection<?> collection) {
        return with(collection).transform(new Transformer<Object, Object>() {
            @Override
            public Object map(Object item) {
                return quote(item);
            }
        }).list();
    }

}
