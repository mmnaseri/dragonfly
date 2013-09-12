package com.agileapes.dragonfly.model;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.TypedMethodModel;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 14:18)
 */
public class EscapeStringMethod extends TypedMethodModel {

    @Invokable
    public String escape(String string) {
        return string.replaceAll("([^\\\\]|^)\"", "$1\\\"");
    }

}
