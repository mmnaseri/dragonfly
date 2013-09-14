<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean class="com.agileapes.dragonfly.entity.impl.HandlerContextPreparatorPostProcessor">
        <property name="mapHandlers">
            <set>
            <#list beans as bean>
                <bean class="${bean}"/>
            </#list>
            </set>
        </property>
    </bean>

</beans>