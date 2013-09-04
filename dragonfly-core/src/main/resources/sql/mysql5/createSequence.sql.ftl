<#if table.primaryKey.columns?size == 1>
<#list table.primaryKey.columns as column>
ALTER TABLE ${qualify(table)} CHANGE ${qualify(column)} ${qualify(column)} ${type(column)}<#if !column.nullable> NOT NULL</#if> AUTO_INCREMENT;</#list></#if>