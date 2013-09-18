<#-- @ftlvariable name="" type="com.agileapes.dragonfly.model.ApplicationContextModel" -->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

<#macro defineBean bean>
    <bean id="${identify(bean)}" class="${bean.type}"<#if bean.arguments?size == 0 && bean.properties?size == 0>/>
<#else>>
    <#list bean.arguments as argument>
        <constructor-arg index="${argument_index}" type="${argument.type}"<#if argument.reference??> ref="${identify(argument.reference)}" /><#elseif !argument.value?is_hash || !argument.value.class?? || argument.value.class.simpleName != "BeanDefinitionModel"> value="${escape(argument.value)}"/><#else>>
            <@defineBean argument.value/>
        </constructor-arg></#if>
    </#list>
    <#list bean.properties as property>
        <property name="${property.name}"<#if property.reference??> ref="${identify(property.reference)}"/><#elseif property.value?is_enumerable>>
            <#if property.list><list><#else><set></#if><#list property.value as item>
            <#if item.class.simpleName == "BeanDefinitionModel"><#if item.id??><ref bean="${item.id}"/>
            <#else><@defineBean item/></#if><#else><value>${item}</value>
            </#if></#list><#if property.list></list><#else>${"\t\t\t"}</set></#if>
        </property>
    <#elseif property.value?is_hash>>
            <map><#list property.value?keys as key>
                <entry key="${escape(key)}"<#if property.value[key]?is_hash><#if property.value[key].class.simpleName == "BeanDefinitionModel">>
                    <@defineBean property.value[key]/>
                </entry><#elseif property.value[key].class.simpleName == "BeanPropertyModel"><#if property.value[key].map>>
                    <map><#list property.value[key].value?keys as subKey>
                        <entry key="${subKey}" value="${property.value[key].value[subKey]}"/>
                    </#list></map>
                </entry>
            <#else><#if property.value[key].reference??> value-ref="${identify(property.value[key].reference)}"/><#else> value="${escape(property.value[key].value)}"/></#if></#if><#else> value="${escape(property.value[key])}" /></#if><#else> value="${escape(property.value[key])}" /></#if>
            </#list></map>
        </property><#else> value="${escape(property.value)}"/></#if>
    </#list>
    </bean>
</#if>
</#macro>
<#list beans as bean>
    <@defineBean bean/>

</#list>

</beans>