UPDATE %{qualify(table)}
SET
<%list isSet(notKey(table.columns)) as column>
%{"\t"}%{qualify(column)} = %{new[column.propertyName]}<%if column_has_next>,</%if>
</%list>
WHERE <%list key(table.columns) as column>%{qualify(column)} = %{old[column.propertyName]}<%if column_has_next> AND </%if></%list>;