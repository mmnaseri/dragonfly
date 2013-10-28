UPDATE %{qualify(table)}
SET
<%assign columns=isSet(isNotGenerated(table.columns))/>
<%list columns as column>
%{"\t"}%{qualify(column)} = %{new[column.propertyName]}<%if column_has_next>,</%if>
</%list>
WHERE <#if table.hasPrimaryKey()><#list table.primaryKey.columns as column>${qualify(column)} = ${old[column.propertyName]}<#if column_has_next> AND </#if></#list><#if table.versionColumn??> AND ${qualify(table.versionColumn)} < ${value[table.versionColumn.name]}</#if><#else><#--
--><%list isSet(isNotGenerated(table.columns)) as column>%{qualify(column)} = %{old[column.propertyName]}<%if column_has_next> AND </%if></%list></#if>;