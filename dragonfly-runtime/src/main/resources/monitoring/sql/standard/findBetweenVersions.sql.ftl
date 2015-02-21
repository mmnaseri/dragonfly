<#-- @ftlvariable name="value" type="com.agileapes.dragonfly.runtime.ext.monitoring.impl.History" -->
<#-- @ftlvariable name="column" type="com.agileapes.dragonfly.statement.impl.model.functions.ColumnPickerMethod" -->
<#-- @ftlvariable name="metadata" type="com.agileapes.dragonfly.metadata.ColumnMetadata" -->
<#-- @ftlvariable name="qualify" type="com.agileapes.dragonfly.statement.impl.model.functions.DatabaseIdentifierQualifierMethod" -->
<#-- @ftlvariable name="table" type="com.agileapes.dragonfly.metadata.TableMetadata" -->
SELECT * FROM ${qualify(table)} WHERE ${qualify(metadata)} = ${value.key} AND ${qualify(table.versionColumn)} > ${value.fromVersion} AND ${qualify(table.versionColumn)} < ${value.toVersion};