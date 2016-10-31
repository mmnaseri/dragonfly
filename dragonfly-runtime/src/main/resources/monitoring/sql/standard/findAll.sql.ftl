<#-- @ftlvariable name="metadata" type="com.mmnaseri.dragonfly.metadata.ColumnMetadata" -->
<#-- @ftlvariable name="qualify" type="com.mmnaseri.dragonfly.statement.impl.model.functions.DatabaseIdentifierQualifierMethod" -->
<#-- @ftlvariable name="table" type="com.mmnaseri.dragonfly.metadata.TableMetadata" -->
SELECT * FROM ${qualify(table)} WHERE ${qualify(metadata)} = ${value.key};