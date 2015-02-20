<#list metadata.columns as local><#assign foreign=local.foreignReference/>ALTER TABLE ${qualify(table)} ADD CONSTRAINT ${escape(metadata.name)} FOREIGN KEY (${escape(local.name)}) REFERENCES ${qualify(foreign.table)} (${escape(foreign.name)});</#list>