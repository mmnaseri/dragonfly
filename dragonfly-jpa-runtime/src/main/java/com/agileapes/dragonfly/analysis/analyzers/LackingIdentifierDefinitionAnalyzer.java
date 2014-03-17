package com.agileapes.dragonfly.analysis.analyzers;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.analysis.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.analysis.DesignIssue;
import com.agileapes.dragonfly.analysis.impl.EntityIssueTarget;
import com.agileapes.dragonfly.analysis.impl.TableIssueTarget;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;

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
