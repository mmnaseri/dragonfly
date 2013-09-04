package com.agileapes.dragonfly.statement.impl.model.functions;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.TypedMethodModel;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/4, 13:43)
 */
public class TypeResolverMethod extends TypedMethodModel {
    
    private final DatabaseDialect dialect;

    public TypeResolverMethod(DatabaseDialect dialect) {
        this.dialect = dialect;
    }

    @Invokable
    public String getTypeOf(ColumnMetadata columnMetadata) {
        return dialect.getType(columnMetadata);
    }
    
}
