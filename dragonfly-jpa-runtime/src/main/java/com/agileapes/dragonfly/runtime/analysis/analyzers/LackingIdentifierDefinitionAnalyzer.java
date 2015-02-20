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
 * @since 1.0 (14/3/17 AD, 15:24)
 */
public class LackingIdentifierDefinitionAnalyzer implements ApplicationDesignAnalyzer {
    
    private final TableMetadataRegistry metadataRegistry;

    public LackingIdentifierDefinitionAnalyzer(TableMetadataRegistry metadataRegistry) {
        this.metadataRegistry = metadataRegistry;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DesignIssue> analyze() {
        return with(metadataRegistry.getEntityTypes())
                .transform(new Transformer<Class<?>, TableMetadata<?>>() {
                    @Override
                    public TableMetadata<?> map(Class<?> entityType) {
                        return metadataRegistry.getTableMetadata(entityType);
                    }
                })
                .keep(new Filter<TableMetadata<?>>() {
                    @Override
                    public boolean accepts(TableMetadata<?> tableMetadata) {
                        return !tableMetadata.hasPrimaryKey();
                    }
                })
                .transform(new Transformer<TableMetadata<?>, DesignIssue>() {
                    @Override
                    public DesignIssue map(TableMetadata<?> tableMetadata) {
                        return new DesignIssue(DesignIssue.Severity.IMPORTANT, new TableIssueTarget(tableMetadata),
                                "Entity does not define a primary key. This might lead to confusion at times when " +
                                        "you are expecting a call to DataAccess.save() to update the entity, while it " +
                                        "has no way of knowing if the entity has been persisted before or not, and such " +
                                        "inserts it instead.", "Add a primary key to all of your entities, unless it is " +
                                "noted carefully that you should always handle update/insert cases manually.");
                    }
                }).list();
    }
    
}
