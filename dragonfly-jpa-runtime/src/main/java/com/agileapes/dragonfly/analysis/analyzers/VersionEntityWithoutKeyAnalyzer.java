package com.agileapes.dragonfly.analysis.analyzers;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.analysis.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.analysis.DesignIssue;
import com.agileapes.dragonfly.analysis.impl.TableIssueTarget;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;

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
