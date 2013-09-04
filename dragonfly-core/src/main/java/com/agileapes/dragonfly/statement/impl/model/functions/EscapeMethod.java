package com.agileapes.dragonfly.statement.impl.model.functions;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.TypedMethodModel;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 13:55)
 */
public class EscapeMethod extends TypedMethodModel {

    private final Character escape;

    public EscapeMethod(Character escape) {
        this.escape = escape;
    }

    @Invokable
    public String escape(String name) {
        return escape + name + escape;
    }

}
