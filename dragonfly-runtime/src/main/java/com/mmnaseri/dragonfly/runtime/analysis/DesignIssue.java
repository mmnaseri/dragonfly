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

package com.mmnaseri.dragonfly.runtime.analysis;

import org.springframework.util.StringUtils;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/3/17 AD, 0:54)
 */
public final class DesignIssue implements Comparable<DesignIssue> {

    public static final String REPORT_FORMAT = "%s issue found in %s\n\tProblem: %s\n\tPossible fix: %s";

    public static enum Severity {
        IMPORTANT, CRITICAL, SEVERE
    }

    private final Severity severity;
    private final IssueTarget target;
    private final String message;
    private final String fix;

    public DesignIssue(Severity severity, IssueTarget target, String message, String fix) {
        this.severity = severity;
        this.target = target;
        this.message = message;
        this.fix = fix;
    }

    public Severity getSeverity() {
        return severity;
    }

    public IssueTarget getTarget() {
        return target;
    }

    public String getMessage() {
        return message;
    }

    public String getFix() {
        return fix;
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") DesignIssue that) {
        if (that == null) {
            return 1;
        }
        int comparison = -1 * this.getSeverity().compareTo(that.getSeverity());
        if (comparison == 0 && getTarget() != null) {
            comparison = this.getTarget().toString().compareTo(that.getTarget().toString());
        }
        if (comparison == 0 && getMessage() != null) {
            comparison = this.getMessage().compareTo(that.getMessage());
        }
        if (comparison == 0 && getFix() != null) {
            comparison = this.getFix().compareTo(that.getFix());
        }
        return comparison;
    }

    @Override
    public String toString() {
        return String.format(REPORT_FORMAT, StringUtils.capitalize(getSeverity().name().toLowerCase()), getTarget(), getMessage(), getFix());
    }

}
