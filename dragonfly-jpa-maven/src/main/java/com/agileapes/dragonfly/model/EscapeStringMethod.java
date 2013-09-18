package com.agileapes.dragonfly.model;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.TypedMethodModel;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 14:18)
 */
public class EscapeStringMethod extends TypedMethodModel {

    @Invokable
    public Object escape(Object input) {
        if (!(input instanceof String)) {
            return input;
        }
        final String string = (String) input;
        return string.replace("\n", "\\n").replaceAll("(^|[^\\\\])\"", "$1\\\\\"");
    }

}
