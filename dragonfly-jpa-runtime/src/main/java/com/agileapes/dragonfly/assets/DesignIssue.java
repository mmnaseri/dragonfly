package com.agileapes.dragonfly.assets;

import org.springframework.util.StringUtils;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 0:54)
 */
public final class DesignIssue implements Comparable<DesignIssue> {

    public static final String REPORT_FORMAT = "%s issue found in %s\n\tProblem: %s\n\tPossible fix: %s";

    public static enum Severity {
        IMPORTANT, CRITICAL, SEVERE
    }

    private final Severity severity;
    private final Object target;
    private final String message;
    private final String fix;

    public DesignIssue(Severity severity, Object target, String message, String fix) {
        this.severity = severity;
        this.target = target;
        this.message = message;
        this.fix = fix;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Object getTarget() {
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
