UPDATE %{qualify(table)}
SET
<%list isSet(isNotKey(table.columns)) as column>
%{"\t"}%{qualify(column)} = %{new[column.propertyName]}<%if column_has_next>,</%if>
</%list>
WHERE <%list table.primaryKey.columns as column>%{qualify(column)} = %{old[column.propertyName]}<%if column_has_next> AND </%if></%list>;