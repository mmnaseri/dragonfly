<#if metadata.generationType == "AUTO">ALTER TABLE ${qualify(table)} CHANGE ${qualify(metadata)} ${qualify(metadata)} ${type(metadata)}<#if !metadata.nullable> NOT NULL</#if></#if>;