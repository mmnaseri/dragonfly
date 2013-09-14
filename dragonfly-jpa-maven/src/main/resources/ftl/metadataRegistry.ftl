<#-- @ftlvariable name="" type="com.agileapes.dragonfly.model.MetadataGenerationModel" -->
package com.agileapes.dragonfly.metadata;

import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.metadata.impl.*;
import com.agileapes.dragonfly.error.NoSuchEntityError;
import com.agileapes.couteau.basics.api.Processor;
import java.util.*;
import javax.annotation.Generated;

@Generated(
    value = "Dragonfly",
    comments = "Generated registry containing table metadata for all " +
               "entities annotated with JPA annotations in the classpath"
)
@SuppressWarnings("unchecked")
public class  GeneratedJpaMetadataRegistry implements MetadataRegistry {

    private final Map<Class<?>, TableMetadata<?>> map = new HashMap<Class<?>, TableMetadata<?>>();

    public GeneratedJpaMetadataRegistry() {
    <#list tables as table>
        {
            final Collection<SequenceMetadata> sequences = new HashSet<SequenceMetadata>();
            final HashSet<ConstraintMetadata> constraints = new HashSet<ConstraintMetadata>();
            final HashSet<NamedQueryMetadata> namedQueries = new HashSet<NamedQueryMetadata>();
            final Set<StoredProcedureMetadata> storedProcedures = new HashSet<StoredProcedureMetadata>();
            final List<ReferenceMetadata<${table.entityType.canonicalName}, ?>> foreignReferences = new ArrayList<ReferenceMetadata<${table.entityType.canonicalName}, ?>>();
            final Collection<ColumnMetadata> tableColumns = new HashSet<ColumnMetadata>();
            <#list table.columns as column>
            final ColumnMetadata column${index(column)} = new ResolvedColumnMetadata(null, ${column.declaringClass.canonicalName}.class, "${escape(column.name)}", ${column.type?c}, "${escape(column.propertyName)}", ${column.propertyType.canonicalName}.class, ${column.nullable?string}, ${column.length?c}, ${column.precision?c}, ${column.scale?c}, <#if column.generationType??>com.agileapes.dragonfly.metadata.ValueGenerationType.${column.generationType}<#else>null</#if>, <#if column.valueGenerator??>"${escape(column.valueGenerator)}"<#else>null</#if>, <#if column.foreignReference??>new UnresolvedColumnMetadata("${escape(column.foreignReference.name)}", new UnresolvedTableMetadata<${column.foreignReference.table.entityType.canonicalName}>(${column.foreignReference.table.entityType.canonicalName}.class))<#else>null</#if>);
            tableColumns.add(column${index(column)});
            </#list>
            final ResolvedTableMetadata<${table.entityType.canonicalName}> tableMetadata = new ResolvedTableMetadata<${table.entityType.canonicalName}>(${table.entityType.canonicalName}.class, "${escape(table.schema)}", "${escape(table.name)}", constraints, tableColumns, namedQueries, sequences, storedProcedures, foreignReferences);
            <#list table.sequences as sequence>
            sequences.add(new DefaultSequenceMetadata("${escape(sequence.name)}", ${sequence.initialValue}, ${sequence.prefetchSize}));
            </#list>
            <#list table.constraints as constraint>
            <#if constraint.class.simpleName == "ForeignKeyConstraintMetadata">
            constraints.add(new ForeignKeyConstraintMetadata(tableMetadata, column${index(constraint.column)}));
            <#elseif constraint.class.simpleName == "PrimaryKeyConstraintMetadata">
            constraints.add(new PrimaryKeyConstraintMetadata(tableMetadata, Arrays.<ColumnMetadata>asList(<#list constraint.columns as column>column${index(column)}<#if column_has_next>, </#if></#list>)));
            <#elseif constraint.class.simpleName == "UniqueConstraintMetadata">
            constraints.add(new UniqueConstraintMetadata(tableMetadata, Arrays.<ColumnMetadata>asList(<#list constraint.columns as column>column${index(column)}<#if column_has_next>, </#if></#list>)));
            </#if>
            </#list>
            <#list table.namedQueries as namedQuery>
            namedQueries.add(new ImmutableNamedQueryMetadata("${escape(namedQuery.name)}", "${escape(namedQuery.query)}"));
            </#list>
            <#list table.procedures as procedure>
            storedProcedures.add(new ImmutableStoredProcedureMetadata("${escape(procedure.name)}", ${procedure.resultType.canonicalName}.class, <#if procedure.parameters?size &gt; 0>Arrays.<ParameterMetadata>asList(
            <#list procedure.parameters as parameter>
                new ImmutableParameterMetadata(com.agileapes.dragonfly.annotations.ParameterMode.${parameter.parameterMode}, ${parameter.type?c}, ${parameter.parameterType.canonicalName}.class)<#if parameter_has_next>,</#if>
            </#list>
            )<#else>new ArrayList<ParameterMetadata>()</#if>));
            </#list>
            <#list table.foreignReferences as reference>
            foreignReferences.add(new ImmutableReferenceMetadata<${table.entityType.canonicalName}, Object>(tableMetadata, "${escape(reference.propertyName)}", null, null, com.agileapes.dragonfly.metadata.RelationType.${reference.relationType}, new ImmutableCascadeMetadata(${reference.cascadeMetadata.cascadePersist()?string}, ${reference.cascadeMetadata.cascadeMerge()?string}, ${reference.cascadeMetadata.cascadeRemove()?string}, ${reference.cascadeMetadata.cascadeRefresh()?string}), ${reference.lazy?string}));
            ((ImmutableReferenceMetadata) foreignReferences.get(foreignReferences.size() - 1)).setForeignColumn(new UnresolvedColumnMetadata("${escape(reference.foreignColumn.name)}", new UnresolvedTableMetadata<${reference.foreignTable.entityType.canonicalName}>(${reference.foreignTable.entityType.canonicalName}.class)));
            </#list>
            map.put(${table.entityType.canonicalName}.class, tableMetadata);
        }
    </#list>
    }

    @Override
    public Collection<Class<?>> getEntityTypes() {
        return map.keySet();
    }

    @Override
    public <E> TableMetadata<E> getTableMetadata(Class<E> entityType) {
        if (map.containsKey(entityType)) {
            //noinspection unchecked
            return (TableMetadata<E>) map.get(entityType);
        }
        throw new NoSuchEntityError(entityType);
    }

    @Override
    public <E> void addTableMetadata(TableMetadata<E> tableMetadata) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Class<?> entityType) {
        return map.containsKey(entityType);
    }

    @Override
    public void setChangeCallback(Processor<MetadataRegistry> registryProcessor) {
    }

}
