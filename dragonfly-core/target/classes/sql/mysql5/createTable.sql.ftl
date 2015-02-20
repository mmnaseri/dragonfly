CREATE TABLE IF NOT EXISTS ${qualify(table)} (<#assign foreignKeys=isReference(table.columns)/><#assign columns=isNotReference(table.columns)/>
<#list columns as column>${"\t"}${qualify(column)} ${type(column)}<#if !column.nullable> NOT NULL</#if><#if column_has_next>,</#if><#if column_has_next || foreignKeys?size == 0>
</#if></#list><#if columns?size != 0 && foreignKeys?size != 0>,</#if><#list foreignKeys as foreignKey>
${"\t"}${qualify(foreignKey)} ${type(foreignKey.foreignReference)}<#if !foreignKey.nullable> NOT NULL</#if><#if foreignKey_has_next>,</#if></#list><#if foreignKeys?size != 0>
</#if>) ENGINE = MyISAM DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;