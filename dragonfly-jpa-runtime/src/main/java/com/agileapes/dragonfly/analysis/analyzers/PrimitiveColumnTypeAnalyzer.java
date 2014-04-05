package com.agileapes.dragonfly.analysis.analyzers;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.collections.CollectionWrapper;
import com.agileapes.dragonfly.analysis.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.analysis.DesignIssue;
import com.agileapes.dragonfly.analysis.impl.ColumnIssueTarget;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;

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
