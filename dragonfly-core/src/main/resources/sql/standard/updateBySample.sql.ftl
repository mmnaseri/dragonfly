UPDATE %{qualify(table)}
SET
<%list isSet(isNotGenerated(table.columns)) as column>
%{"\t"}%{qualify(column)} = %{new[column.propertyName]}<%if column_has_next>,</%if>
</%list>
WHERE <#if table.hasPrimaryKey()><%list table.primaryKey.columns as column>%{qualify(column)} = %{old[column.propertyName]}<%if column_has_next> AND </%if></%list><#else><#--
--><%list isSet(isNotGenerated(table.columns)) as column>%{qualify(column)} = %{old[column.propertyName]}<%if column_has_next> AND </%if></%list></#if>;