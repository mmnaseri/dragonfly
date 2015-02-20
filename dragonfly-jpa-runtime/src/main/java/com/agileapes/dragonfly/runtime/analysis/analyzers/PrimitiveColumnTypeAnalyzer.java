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

package com.agileapes.dragonfly.runtime.analysis.analyzers;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.collections.CollectionWrapper;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.runtime.analysis.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.runtime.analysis.DesignIssue;
import com.agileapes.dragonfly.runtime.analysis.impl.ColumnIssueTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 15:44)
 */
public class PrimitiveColumnTypeAnalyzer implements ApplicationDesignAnalyzer {

    private final TableMetadataRegistry metadataRegistry;

    public PrimitiveColumnTypeAnalyzer(TableMetadataRegistry metadataRegistry) {
        this.metadataRegistry = metadataRegistry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DesignIssue> analyze() {
        final ArrayList<DesignIssue> issues = new ArrayList<DesignIssue>();
        final CollectionWrapper<Collection<ColumnMetadata>> transform = with(metadataRegistry.getEntityTypes())
                .transform(new Transformer<Class<?>, TableMetadata<?>>() {
                    @Override
                    public TableMetadata<?> map(Class<?> entityType) {
                        return metadataRegistry.getTableMetadata(entityType);
                    }
                })
                .transform(new Transformer<TableMetadata<?>, Collection<ColumnMetadata>>() {
                    @Override
                    public Collection<ColumnMetadata> map(TableMetadata<?> tableMetadata) {
                        return with(tableMetadata.getColumns()).keep(new Filter<ColumnMetadata>() {
                            @Override
                            public boolean accepts(ColumnMetadata columnMetadata) {
                                return columnMetadata.getPropertyType().isPrimitive();
                            }
                        }).list();
                    }
                });
        transform
                .drop(new Filter<Collection<ColumnMetadata>>() {
                    @Override
                    public boolean accepts(Collection<ColumnMetadata> metadataCollection) {
                        return metadataCollection.isEmpty();
                    }
                })
                .each(new Processor<Collection<ColumnMetadata>>() {
                    @Override
                    public void process(Collection<ColumnMetadata> metadataCollection) {
                        issues.addAll(with(metadataCollection)
                                .transform(new Transformer<ColumnMetadata, DesignIssue>() {
                                    @Override
                                    public DesignIssue map(ColumnMetadata columnMetadata) {
                                        return new DesignIssue(DesignIssue.Severity.CRITICAL, new ColumnIssueTarget(columnMetadata),
                                                "Column property type is defined as a primitive type rather than a class type. " +
                                                        "This can cause many confusing NullPointerException cases being thrown " +
                                                        "due to automatic boxing and unboxing of values and is rather hard to " +
                                                        "trace back to the root of the problem.", "Always try using class versions " +
                                                "of primitive types, e.g. java.lang.Integer for int, java.lang.Double for double, etc.");
                                    }
                                })
                                .list());
                    }
                });
        return issues;
    }

}
