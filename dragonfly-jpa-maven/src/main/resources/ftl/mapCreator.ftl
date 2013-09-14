<#-- @ftlvariable name="" type="com.agileapes.dragonfly.model.BeanMapperModel" -->
package ${entityType.canonicalName?substring(0, entityType.canonicalName?last_index_of("."))};

import com.agileapes.dragonfly.entity.EntityMapHandler;

import java.util.Map;
import java.util.HashMap;
import javax.annotation.Generated;

@Generated(
    value = "Dragonfly",
    comments = "Entity map handler for ${entityType.canonicalName}"
)
public class ${entityType.simpleName}MapHandler implements EntityMapHandler<${entityType.canonicalName}> {

    @Override
    public Class<${entityType.canonicalName}> getEntityType() {
        return ${entityType.canonicalName}.class;
    }

    @Override
    public Map<String, Object> toMap(${entityType.canonicalName} entity) {
        final Map<String, Object> map = new HashMap<String, Object>();
        <#list properties as property>
        Object value${property_index} = <#if property.declaringClass.canonicalName == entityType.canonicalName>entity<#else>((${property.declaringClass.canonicalName}) entity)</#if>.${property.getterName}();
        if (value${property_index} != null) {<#if property.foreignProperty??>
            value${property_index} = ((${property.foreignProperty.declaringClass.canonicalName}) value${property_index}).${property.foreignProperty.getterName}();
        <#else>

        </#if>
        <#if property.temporalType??>
            value${property_index} = new java.sql.${property.temporalType?string?lower_case?cap_first}(((java.util.Date) value${property_index}).getTime());
        </#if>
            map.put("${property.propertyName}", value${property_index});
        }
        </#list>
        return map;
    }

    @Override
    public ${entityType.canonicalName} fromMap(${entityType.canonicalName} entity, Map<String, Object> map) {
        <#list properties as property><#if property.foreignProperty??><#else>
        if (map.containsKey("${property.columnName}")) {
            <#if property.declaringClass.canonicalName == entityType.canonicalName>entity<#else>((${property.declaringClass.canonicalName}) entity)</#if>.${property.setterName}((${property.propertyType.canonicalName}) map.get("${property.columnName}"));
        }
        </#if></#list>
        return entity;
    }

}
