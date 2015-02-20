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
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.runtime.analysis.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.runtime.analysis.DesignIssue;
import com.agileapes.dragonfly.runtime.analysis.impl.TableIssueTarget;

import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 14:53)
 */
public class VersionEntityWithoutKeyAnalyzer implements ApplicationDesignAnalyzer {

    private final TableMetadataRegistry metadataContext;

    public VersionEntityWithoutKeyAnalyzer(TableMetadataRegistry metadataContext) {
        this.metadataContext = metadataContext;
    }

    @Override
    public List<DesignIssue> analyze() {
        //noinspection unchecked
        return with(metadataContext.getEntityTypes())
                .transform(new Transformer<Class<?>, TableMetadata<?>>() {
                    @Override
                    public TableMetadata<?> map(Class<?> entityType) {
                        return metadataContext.getTableMetadata(entityType);
                    }
                })
                .keep(new Filter<TableMetadata<?>>() {
                    @Override
                    public boolean accepts(TableMetadata<?> tableMetadata) {
                        return tableMetadata.getVersionColumn() != null && !tableMetadata.hasPrimaryKey();
                    }
                })
                .transform(new Transformer<TableMetadata<?>, DesignIssue>() {
                    @Override
                    public DesignIssue map(TableMetadata<?> tableMetadata) {
                        return new DesignIssue(DesignIssue.Severity.SEVERE, new TableIssueTarget(tableMetadata),
                                "Entities without an identity column cannot have a version column. This will result in" +
                                        "runtime errors that are sometimes hard to debug.", "Add an identity column to " +
                                "your entity alongside its version column. It might be that you expected the identity " +
                                "column to be added through some extension that is not doing what it is supposed to.");
                    }
                }).list();
    }

}
