<#-- @ftlvariable name="column" type="com.mmnaseri.dragonfly.statement.impl.model.functions.ColumnPickerMethod" -->
<#-- @ftlvariable name="value" type="com.mmnaseri.dragonfly.runtime.ext.monitoring.impl.History" -->
<#-- @ftlvariable name="metadata" type="com.mmnaseri.dragonfly.metadata.ColumnMetadata" -->
<#-- @ftlvariable name="qualify" type="com.mmnaseri.dragonfly.statement.impl.model.functions.DatabaseIdentifierQualifierMethod" -->
<#-- @ftlvariable name="table" type="com.mmnaseri.dragonfly.metadata.TableMetadata" -->
SELECT * FROM ${qualify(table)} WHERE ${qualify(metadata)} = ${value.key} AND ${qualify(column('hist_operation'))} = ${value.operation};