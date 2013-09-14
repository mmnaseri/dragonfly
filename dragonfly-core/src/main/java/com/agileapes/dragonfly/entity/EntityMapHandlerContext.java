package com.agileapes.dragonfly.entity;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:31)
 */
public interface EntityMapHandlerContext extends EntityMapCreator, MapEntityCreator {

    void addMapHandler(EntityMapHandler<?> mapHandler);

}
