<#-- @ftlvariable name="" type="com.agileapes.dragonfly.model.ApplicationContextModel" -->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
<#list beans as bean>
    <bean<#if bean.id != bean.type> id="${bean.id}"</#if> class="${bean.type}"<#if bean.properties?size == 0 && bean.references?size == 0>/>
<#else>>
<#list bean.properties?keys as property>
        <property name="${property}" value="${bean.properties[property]}"/>
</#list>
<#list bean.references?keys as property>
        <property name="${property}" ref="${bean.references[property]}"/>
</#list>
    </bean>
</#if>
</#list>

    <bean id="_securityManager" class="com.agileapes.dragonfly.security.impl.DefaultDataSecurityManager">
        <constructor-arg ref="_accessDeniedHandler"/>
    </bean>

    <bean id="_generatedStatementRegistry" class="com.agileapes.dragonfly.mojo.GeneratedStatementRegistry">
        <constructor-arg index="0" type="com.agileapes.dragonfly.dialect.DatabaseDialect" ref="_databaseDialect"/>
        <constructor-arg index="1" type="com.agileapes.dragonfly.metadata.MetadataRegistry" ref="metadataContext"/>
    </bean>

    <bean class="com.agileapes.dragonfly.data.DataAccessSession">
        <constructor-arg index="0" type="com.agileapes.dragonfly.dialect.DatabaseDialect" ref="_databaseDialect"/>
        <constructor-arg index="1" type="com.agileapes.dragonfly.statement.impl.StatementRegistry" ref="_generatedStatementRegistry"/>
        <constructor-arg index="2" type="com.agileapes.dragonfly.metadata.MetadataRegistry" ref="metadataContext"/>
        <constructor-arg index="3" type="javax.sql.DataSource" ref="${r"${db.dataSource}"}"/>
        <constructor-arg index="4" type="java.lang.String" value="${r"${db.username}"}"/>
        <constructor-arg index="5" type="java.lang.String" value="${r"${db.password}"}"/>
    </bean>

</beans>