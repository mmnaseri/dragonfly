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

package com.mmnaseri.dragonfly.fluent.impl;

import com.mmnaseri.dragonfly.fluent.JoinQueryCondition;
import com.mmnaseri.dragonfly.fluent.JoinQueryExpander;
import com.mmnaseri.dragonfly.fluent.generation.BookKeeper;
import com.mmnaseri.dragonfly.fluent.generation.JoinType;
import com.mmnaseri.dragonfly.fluent.generation.impl.DefaultBookKeeper;
import com.mmnaseri.dragonfly.metadata.TableMetadata;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/10 AD, 12:40)
 */
public class DefaultJoinQueryCondition<E, G> implements JoinQueryCondition<E> {

    private final DefaultSelectQueryInitiator<E> initializer;
    private final JoinType joinType;
    private final BookKeeper<G> bookKeeper;

    public DefaultJoinQueryCondition(DefaultSelectQueryInitiator<E> initializer, JoinType joinType, G entity, TableMetadata<G> tableMetadata) {
        this.initializer = initializer;
        this.joinType = joinType;
        this.bookKeeper = new DefaultBookKeeper<G>(entity, tableMetadata);
    }

    @Override
    public <F> JoinQueryExpander<E, F> when(F property) {
        return new DefaultJoinQueryExpander<G, E, F>(initializer, joinType, bookKeeper, property);
    }

}
