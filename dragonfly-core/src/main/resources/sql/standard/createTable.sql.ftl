CREATE TABLE IF NOT EXISTS ${qualify(table)} (
<#list table.columns as column>${"\t"}${qualify(column)} ${type(column)}<#if !column.nullable> NOT NULL</#if><#if column_has_next>,</#if>
</#list>);