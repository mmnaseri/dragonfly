/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.statement.impl.model;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a template model that will discover all parameters in a certain namespace and turn them up
 * for inspection and evaluation.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:03)
 */
public class ParameterPlaceholderNamespace implements TemplateHashModel {

    private final List<String> parameters = new ArrayList<String>();

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        return new ParameterHolder(key, this);
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return false;
    }

    public List<String> getParameters() {
        return parameters;
    }

    private static class ParameterHolder implements TemplateHashModel, TemplateScalarModel {

        private final String prefix;
        private final ParameterPlaceholderNamespace namespace;

        private ParameterHolder(String prefix, ParameterPlaceholderNamespace namespace) {
            this.prefix = prefix;
            this.namespace = namespace;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            return new ParameterHolder(prefix + "." + key, namespace);
        }

        @Override
        public boolean isEmpty() throws TemplateModelException {
            return false;
        }

        @Override
        public String getAsString() throws TemplateModelException {
            namespace.parameters.add(prefix);
            return "?";
        }

    }

}
