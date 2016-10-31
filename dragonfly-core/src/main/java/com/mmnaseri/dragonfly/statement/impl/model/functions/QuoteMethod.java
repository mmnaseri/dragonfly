/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.statement.impl.model.functions;

import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.couteau.freemarker.api.Invokable;
import com.mmnaseri.couteau.freemarker.model.TypedMethodModel;

import java.util.Collection;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * Quotes identifiers according to dialectic rules and conventions
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
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
