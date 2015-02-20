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

package com.agileapes.dragonfly.ext.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.api.impl.CastingTransformer;
import com.agileapes.couteau.basics.api.impl.NegatingFilter;
import com.agileapes.dragonfly.error.ExpressionParseError;
import com.agileapes.dragonfly.ext.ExtensionExpressionParser;
import com.agileapes.dragonfly.ext.impl.parser.*;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.BufferedTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * This is the default extension expression used throughout the application
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 15:53)
 */
@SuppressWarnings("unchecked")
public class DefaultExtensionExpressionParser implements ExtensionExpressionParser {

    public static final Filter<Class<?>> ALL = new Filter<Class<?>>() {
        @Override
        public boolean accepts(Class<?> item) {
            return true;
        }
    };

    @Override
    public Filter<Class<?>> map(String expression) {
        final TypeDescriptorParser parser = new TypeDescriptorParser(new BufferedTokenStream(new TypeDescriptorLexer(new ANTLRStringStream(expression))));
        try {
            final TypeDescriptorParser.start_return start = parser.start();
            final Filter<Class<?>> filter = getFilter((CommonTree) start.getTree());
            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new RecognitionException();
            }
            return filter;
        } catch (RecognitionException e) {
            throw new ExpressionParseError(e);
        }
    }

    private Filter<Class<?>> getType(List<CommonTree> items) {
        final CommonTree first = items.remove(0);
        if (first.getType() == TypeDescriptorParser.TYPE) {
            final TypeSelector typeSelector;
            if (first.getChildCount() == 1 && first.getChild(0).getType() == TypeDescriptorParser.SOMETHING) {
                return ALL;
            }
            if (items.isEmpty() || items.get(0).getType() != TypeDescriptorParser.CHILD) {
                typeSelector = TypeSelector.EQUALS;
            } else {
                items.remove(0);
                typeSelector = TypeSelector.ASSIGNABLE;
            }
            return new TypeFilter(convertTree(first), typeSelector);
        }
        throw new ExpressionParseError("Parse error in expression at " + first.getTokenStartIndex());
    }

    private Filter<Class<?>> getFilter(CommonTree item) {
        if (item.getType() == TypeDescriptorParser.SOMETHING) {
            return ALL;
        } else if (item.getType() == TypeDescriptorParser.SELECT) {
            final ArrayList<CommonTree> trees = new ArrayList<CommonTree>(item.getChildren());
            final List<Filter<Class<?>>> annotations = getAnnotations(trees);
            return new AndTypeFilter(with(getType(trees)).add(new Filter<Class<?>>() {
                @Override
                public boolean accepts(Class<?> item) {
                    for (Filter<Class<?>> filter : annotations) {
                        boolean found = false;
                        for (Annotation annotation : item.getAnnotations()) {
                            if (filter.accepts(annotation.annotationType())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            return false;
                        }
                    }
                    return true;
                }
            }).list());
        } else if (item.getType() == TypeDescriptorParser.NOT) {
            return new NegatingFilter<Class<?>>(getFilter((CommonTree) item.getChild(0)));
        } else if (item.getType() == TypeDescriptorParser.AND) {
            return getAnd(item);
        } else if (item.getType() == TypeDescriptorParser.OR) {
            return getOr(item);
        } else if (item.getType() == TypeDescriptorParser.PROPERTY) {
            return getProperty(with(item.getChildren()).transform(new CastingTransformer(CommonTree.class)).list());
        } else if (item.getType() == TypeDescriptorParser.METHOD) {
            return getMethod(with(item.getChildren()).transform(new CastingTransformer(CommonTree.class)).list());
        }
        return null;
    }

    private Filter<Class<?>> getMethod(List<CommonTree> items) {
        return new HavingMethodFilter(getAnnotations(items), getType(items), getIdentifier(items), getParameters(items));
    }

    private Filter<Class<?>> getAnd(CommonTree tree) {
        final ArrayList<Filter<Class<?>>> filters = new ArrayList<Filter<Class<?>>>();
        for (CommonTree item : ((Collection<CommonTree>) tree.getChildren())) {
            filters.add(getFilter(item));
        }
        return new AndTypeFilter(filters);
    }

    private Filter<Class<?>> getOr(CommonTree tree) {
        final ArrayList<Filter<Class<?>>> filters = new ArrayList<Filter<Class<?>>>();
        for (CommonTree item : ((Collection<CommonTree>) tree.getChildren())) {
            filters.add(getFilter(item));
        }
        return new OrTypeFilter(filters);
    }

    private List<Filter<Class<?>>> getParameters(List<CommonTree> items) {
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        final CommonTree first = items.remove(0);
        if (first.getType() == TypeDescriptorParser.ANYTHING) {
            return null;
        }
        if (first.getType() != TypeDescriptorParser.PARAMS) {
            throw new ExpressionParseError("Parse error in expression at " + first.getTokenStartIndex());
        }
        final ArrayList<Filter<Class<?>>> filters = new ArrayList<Filter<Class<?>>>();
        final ArrayList<CommonTree> children = new ArrayList<CommonTree>(first.getChildren());
        while (!children.isEmpty()) {
            filters.add(getType(children));
        }
        return filters;
    }

    private String getIdentifier(List<CommonTree> items) {
        final CommonTree first = items.remove(0);
        if (first.getType() == TypeDescriptorParser.SOMETHING) {
            return ".*";
        }
        return first.getText();
    }

    private List<Filter<Class<?>>> getAnnotations(List<CommonTree> items) {
        final CommonTree first = items.get(0);
        if (first.getType() != TypeDescriptorParser.ANNOTATION) {
            return Collections.emptyList();
        }
        items.remove(0);
        return getAnnotationFilters(first.getChildren());
    }

    private List<Filter<Class<?>>> getAnnotationFilters(List<CommonTree> annotations) {
        final ArrayList<Filter<Class<?>>> filters = new ArrayList<Filter<Class<?>>>();
        for (CommonTree annotation : annotations) {
            filters.add(new TypeFilter(convertTree(annotation), TypeSelector.EQUALS));
        }
        return filters;
    }

    private List<String> convertTree(CommonTree tree) {
        return with(tree.getChildren()).transform(new Transformer<Object, String>() {
            @Override
            public String map(Object input) {
                return ((Tree) input).getText();
            }
        }).list();
    }

    public Filter<Class<?>> getProperty(List<CommonTree> items) {
        return new HavingPropertyFilter(getAnnotations(items), getType(items), getIdentifier(items));
    }

}
