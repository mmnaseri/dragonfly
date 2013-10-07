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

import java.util.ArrayList;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
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
    public Filter<Class<?>> parse(String expression) {
        final TypeDescriptorParser parser = new TypeDescriptorParser(new BufferedTokenStream(new TypeDescriptorLexer(new ANTLRStringStream(expression))));
        try {
            final TypeDescriptorParser.start_return start = parser.start();
            final Filter<Class<?>> filter = createFilter((CommonTree) start.getTree());
            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new RecognitionException();
            }
            return filter;
        } catch (RecognitionException e) {
            throw new ExpressionParseError(e);
        }
    }

    private Filter<Class<?>> createFilter(CommonTree tree) {
        if (tree.getType() == TypeDescriptorParser.SELECT) {
            final List<CommonTree> children = tree.getChildren();
            if (children.size() == 1) {
                final CommonTree child = children.get(0);
                if (child.getType() == TypeDescriptorParser.ANY) {
                    return ALL;
                } else if (child.getType() == TypeDescriptorParser.TYPE) {
                    return new TypeFilter(convertTree(child), TypeSelector.EQUALS);
                }
            } else if (children.size() == 2) {
                final CommonTree first = children.get(0);
                final CommonTree second = children.get(1);
                if (first.getType() == TypeDescriptorParser.TYPE) {
                    if (second.getType() != TypeDescriptorParser.CHILD) {
                        throw new ExpressionParseError(new Error("Invalid token: " + second.getToken()));
                    }
                    return new TypeFilter(convertTree(first), TypeSelector.ASSIGNABLE);
                } else {
                    if (first.getType() == TypeDescriptorParser.ANNOTATION) {
                        final ArrayList<Filter<Class<?>>> filters = new ArrayList<Filter<Class<?>>>();
                        if (second.getType() == TypeDescriptorParser.TYPE) {
                            filters.add(new TypeFilter(convertTree(second), TypeSelector.EQUALS));
                        }
                        filters.addAll(getAnnotationFilters(first.getChildren()));
                        return new AllTypeFilter(filters);
                    }
                    throw new ExpressionParseError(new Error("Invalid token: " + second.getToken()));
                }
            } else if (children.size() == 3) {
                final CommonTree first = children.get(0);
                final CommonTree second = children.get(1);
                final ArrayList<Filter<Class<?>>> filters = new ArrayList<Filter<Class<?>>>();
                filters.add(new TypeFilter(convertTree(second), TypeSelector.ASSIGNABLE));
                filters.addAll(getAnnotationFilters(first.getChildren()));
                return new AllTypeFilter(filters);
            }
        } else if (tree.getType() == TypeDescriptorParser.NOT) {
            return new NegatingFilter<Class<?>>(createFilter((CommonTree) tree.getChildren().get(0)));
        } else if (tree.getType() == TypeDescriptorParser.AND) {
            return new AllTypeFilter(with(tree.getChildren()).transform(new CastingTransformer(CommonTree.class)).transform(new Transformer<CommonTree, Filter<Class<?>>>() {
                @Override
                public Filter<Class<?>> map(CommonTree input) {
                    return createFilter(input);
                }
            }).list());
        } else if (tree.getType() == TypeDescriptorParser.OR) {
            return new AnyTypeFilter(with(tree.getChildren()).transform(new CastingTransformer(CommonTree.class)).transform(new Transformer<CommonTree, Filter<Class<?>>>() {
                @Override
                public Filter<Class<?>> map(CommonTree input) {
                    return createFilter(input);
                }
            }).list());
        }
        return ALL;
    }

    private List<Filter<Class<?>>> getAnnotationFilters(List<CommonTree> annotations) {
        final ArrayList<Filter<Class<?>>> filters = new ArrayList<Filter<Class<?>>>();
        for (CommonTree annotation : annotations) {
            filters.add(new AnnotationTypeFilter(convertTree(annotation)));
        }
        return filters;
    }

    private List convertTree(CommonTree tree) {
        return with(tree.getChildren()).transform(new Transformer<Object, String>() {
            @Override
            public String map(Object input) {
                return ((Tree) input).getText();
            }
        }).list();
    }
}
