<#-- @ftlvariable name="" type="com.agileapes.dragonfly.model.StatementGenerationModel" -->
package com.agileapes.dragonfly.statement;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;
import com.agileapes.dragonfly.statement.impl.ImmutableStatement;
import com.agileapes.dragonfly.statement.impl.ProcedureCallStatement;

import javax.annotation.Generated;

@Generated(
    value = "Dragonfly",
    comments = "The automatically generated statement registry that will hold " +
               "prepared statement metadata."
)
public class GeneratedStatementRegistry extends StatementRegistry {

    public GeneratedStatementRegistry(DatabaseDialect dialect, MetadataRegistry metadataRegistry) {
        try {
<#list statements?keys as name>
            <#assign statement=statements[name] />register("${escape(name)}", <#if statement.class.simpleName == "ProcedureCallStatement">new ProcedureCallStatement(metadataRegistry.getTableMetadata(${statement.tableMetadata.entityType.canonicalName}.class), dialect, "${escape(statement.sql)}")<#else>new ImmutableStatement(metadataRegistry.getTableMetadata(${statement.tableMetadata.entityType.canonicalName}.class), dialect, "${escape(statement.sql)}", ${statement.dynamic?string}, ${statement.hasParameters()?string}, com.agileapes.dragonfly.statement.StatementType.${statement.type})</#if>);
</#list>
        } catch (Exception e) {
            throw new MetadataCollectionError("Failed to register statements", e);
        }
    }

}
