<#-- @ftlvariable name="" type="com.agileapes.dragonfly.model.ApplicationContextModel" -->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
<#macro defineBean bean>
        <bean id="${identify(bean)}" class="${bean.type}"<#if bean.arguments?size == 0 && bean.properties?size == 0>/>
<#else>>
    <#list bean.arguments as argument>
        <constructor-arg index="${argument_index}" type="${argument.type}" <#if argument.reference??>ref="${identify(argument.reference)}"<#else>value="${escape(argument.value)}"</#if>/>
    </#list>
    <#list bean.properties as property>
            <property name="${property.name}" <#if property.reference??>ref="${identify(property.reference)}"/><#elseif property.value?is_enumerable>>
                <#if property.list><list><#else><set></#if><#list property.value as item>
                    <#if item.class.simpleName == "BeanDefinitionModel"><#if item.id??><ref bean="${item.id}"/><#else><@defineBean item/></#if><#else><value>${item}</value></#if>
                </#list><#if property.list></list><#else></set></#if>
        </property>
    <#else>value="${escape(property.value)}"/></#if>
    </#list>
    </bean>
</#if>

</#macro>
<#list beans as bean>
    <@defineBean bean/>
</#list>

</beans>