<#-- @ftlvariable name="" type="com.agileapes.dragonfly.model.StatementGenerationModel" -->
package com.agileapes.dragonfly.mojo;

import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;

import javax.annotation.Generated;

@Generated(
    value = "Dragonfly",
    comments = "The automatically generated statement registry that will hold " +
               "prepared statement metadata."
)
public class GeneratedStatementRegistry extends StatementRegistry {

    public GeneratedStatementRegistry(DatabaseDialect dialect, MetadataRegistry metadataRegistry) {
        try {
<#list statements as statement></#list>
        } catch (RegistryException e) {
            throw new MetadataCollectionError("Failed to register statements", e);
        }
    }

}
