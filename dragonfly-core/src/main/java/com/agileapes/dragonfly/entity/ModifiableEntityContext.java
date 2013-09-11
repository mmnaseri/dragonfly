package com.agileapes.dragonfly.entity;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 21:24)
 */
public interface ModifiableEntityContext extends EntityContext {

    <I> void addInterface(Class<I> ifc, Class<? extends I> implementation);

}
