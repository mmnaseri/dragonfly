package com.agileapes.dragonfly.assets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 2:09)
 */
public final class ComplexDesignIssueTarget {

    private final List<Object> involvedParties = new ArrayList<Object>();

    public ComplexDesignIssueTarget(Collection<?> involvedParties) {
        this.involvedParties.addAll(involvedParties);
    }

    public ComplexDesignIssueTarget(Object... involvedParties) {
        this.involvedParties.addAll(with(involvedParties).list());
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
