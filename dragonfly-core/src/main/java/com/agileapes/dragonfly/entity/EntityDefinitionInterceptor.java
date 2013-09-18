package com.agileapes.dragonfly.entity;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 14:13)
 */
public interface EntityDefinitionInterceptor {

    <E> EntityDefinition<E> intercept(EntityDefinition<E> definition);

}
