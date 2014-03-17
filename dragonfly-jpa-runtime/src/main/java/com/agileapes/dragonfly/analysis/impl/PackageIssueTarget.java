package com.agileapes.dragonfly.analysis.impl;

import com.agileapes.dragonfly.analysis.IssueTarget;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 15:40)
 */
public class PackageIssueTarget implements IssueTarget<String> {

    private final String packageName;

    public PackageIssueTarget(String packageName) {
        if (packageName.endsWith(".")) {
            packageName += "*";
        }
        this.packageName = packageName;
    }

    @Override
    public String getTarget() {
        return packageName;
    }

    @Override
    public String toString() {
        return "package '" + packageName + "'";
    }
}
