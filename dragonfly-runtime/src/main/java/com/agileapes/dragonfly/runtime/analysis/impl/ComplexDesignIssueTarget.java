/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
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

package com.agileapes.dragonfly.runtime.analysis.impl;

import com.agileapes.dragonfly.runtime.analysis.IssueTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 2:09)
 */
public final class ComplexDesignIssueTarget implements IssueTarget<Collection<IssueTarget<?>>> {

    private final List<IssueTarget<?>> involvedParties = new ArrayList<IssueTarget<?>>();

    public ComplexDesignIssueTarget(Collection<IssueTarget<?>> involvedParties) {
        this.involvedParties.addAll(involvedParties);
    }

    public ComplexDesignIssueTarget(IssueTarget<?>... involvedParties) {
        this.involvedParties.addAll(with(involvedParties).list());
    }

    @Override
    public Collection<IssueTarget<?>> getTarget() {
        return Collections.unmodifiableCollection(involvedParties);
    }

    @Override
    public String toString() {
        if (involvedParties.isEmpty()) {
            return "unknown target";
        }
        if (involvedParties.size() == 1) {
            return involvedParties.get(0).toString();
        }
        if (involvedParties.size() == 2) {
            return involvedParties.get(0) + " and " + involvedParties.get(1);
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < involvedParties.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            if (i == involvedParties.size() - 1) {
                builder.append("and ");
            }
            builder.append(involvedParties.get(i));
        }
        return builder.toString();
    }
}
