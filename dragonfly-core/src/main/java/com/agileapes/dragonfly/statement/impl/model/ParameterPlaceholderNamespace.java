package com.agileapes.dragonfly.statement.impl.model;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

import java.util.ArrayList;
import java.util.List;

/**
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
